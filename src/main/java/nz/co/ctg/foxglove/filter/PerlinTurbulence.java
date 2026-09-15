package nz.co.ctg.foxglove.filter;

/**
 * The Perlin noise generator {@code feTurbulence} is defined by (SVG 1.1 15.24) - transcribed line-for-line from the specification's own C reference implementation, constant and
 * variable names included, so it can be checked directly against the spec text rather than trusted on faith. This is deliberately <b>not</b> a generic/idiomatic Perlin noise
 * implementation: {@code feTurbulence} is a bit-exact, deterministic algorithm (the same {@code seed} must produce the same pixels in every conformant renderer), so fidelity to
 * the reference code - including its specific pseudo-random generator, lattice/gradient setup order, and the way the channel-fill loop and the lattice shuffle deliberately share
 * one loop variable and one running seed sequence - matters more than idiom.
 */
final class PerlinTurbulence {

    /** One {@code turbulence()} call's stitch state, fresh at the start of every call and mutated across its own octave loop only - see {@link #turbulence}. */
    record StitchInfo(int width, int wrapX, int height, int wrapY) {
    }

    // Park-Miller "minimal standard" generator via Schrage's method (safe in 32-bit arithmetic by construction) -
    // spec: "Produces results in the range [1, 2**31 - 2]... r = (a * r) mod m where a = 16807 and m = 2**31 - 1".
    private static final long RAND_M = 2147483647L;
    private static final long RAND_A = 16807L;
    private static final long RAND_Q = 127773L;
    private static final long RAND_R = 2836L;

    private static final int B_SIZE = 0x100;
    private static final int BM = 0xff;

    /**
     * Package-visible: {@link SvgFilterRasterPipeline} needs it too, to compute a {@code stitchTiles="stitch"} primitive's initial wrap values the same way {@link #turbulence}
     * does.
     */
    static final int PERLIN_N = 0x1000;

    private final int[] latticeSelector = new int[B_SIZE + B_SIZE + 2];
    private final double[][][] gradient = new double[4][B_SIZE + B_SIZE + 2][2];

    /**
     * Builds the lattice permutation and per-channel gradient tables for {@code seedAttribute} - the {@code seed} attribute's raw value, truncated toward zero here exactly as the
     * specification requires ("must first be truncated, i.e. rounded to the closest integer value towards zero"), which a narrowing {@code double}-to-{@code long} cast already
     * does. Construction is the expensive part (2048 pseudo-random draws plus a shuffle) - one instance is built per {@code feTurbulence} evaluation and reused for every pixel and
     * channel.
     */
    PerlinTurbulence(double seedAttribute) {
        long seed = setupSeed((long) seedAttribute);
        int i = 0;
        for (int k = 0; k < 4; k++) {
            for (i = 0; i < B_SIZE; i++) {
                latticeSelector[i] = i;
                for (int j = 0; j < 2; j++) {
                    seed = random(seed);
                    gradient[k][i][j] = ((seed % (B_SIZE + B_SIZE)) - B_SIZE) / (double) B_SIZE;
                }
                double s = Math.sqrt(gradient[k][i][0] * gradient[k][i][0] + gradient[k][i][1] * gradient[k][i][1]);
                gradient[k][i][0] /= s;
                gradient[k][i][1] /= s;
            }
        }
        // Deliberately reuses `i`, left at B_SIZE by the loop above - the spec's own reference code shares one loop
        // variable and one running seed sequence across the channel fill and this shuffle.
        while (--i > 0) {
            int k = latticeSelector[i];
            seed = random(seed);
            int j = (int) (seed % B_SIZE);
            latticeSelector[i] = latticeSelector[j];
            latticeSelector[j] = k;
        }
        for (i = 0; i < B_SIZE + 2; i++) {
            latticeSelector[B_SIZE + i] = latticeSelector[i];
            for (int k = 0; k < 4; k++) {
                for (int j = 0; j < 2; j++) {
                    gradient[k][B_SIZE + i][j] = gradient[k][i][j];
                }
            }
        }
    }

    private static long setupSeed(long seed) {
        if (seed <= 0) {
            seed = -(seed % (RAND_M - 1)) + 1;
        }
        if (seed > RAND_M - 1) {
            seed = RAND_M - 1;
        }
        return seed;
    }

    private static long random(long seed) {
        long result = RAND_A * (seed % RAND_Q) - RAND_R * (seed / RAND_Q);
        if (result <= 0) {
            result += RAND_M;
        }
        return result;
    }

    private static double sCurve(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    /** Standard 2D Perlin noise at {@code (vecX, vecY)} for one colour channel, optionally wrapped for seamless tiling. */
    private double noise2(int channel, double vecX, double vecY, StitchInfo stitch) {
        double t = vecX + PERLIN_N;
        int bx0 = (int) t;
        int bx1 = bx0 + 1;
        double rx0 = t - (int) t;
        double rx1 = rx0 - 1.0;
        t = vecY + PERLIN_N;
        int by0 = (int) t;
        int by1 = by0 + 1;
        double ry0 = t - (int) t;
        double ry1 = ry0 - 1.0;

        if (stitch != null) {
            if (bx0 >= stitch.wrapX()) {
                bx0 -= stitch.width();
            }
            if (bx1 >= stitch.wrapX()) {
                bx1 -= stitch.width();
            }
            if (by0 >= stitch.wrapY()) {
                by0 -= stitch.height();
            }
            if (by1 >= stitch.wrapY()) {
                by1 -= stitch.height();
            }
        }
        bx0 &= BM;
        bx1 &= BM;
        by0 &= BM;
        by1 &= BM;

        int i = latticeSelector[bx0];
        int j = latticeSelector[bx1];
        int b00 = latticeSelector[i + by0];
        int b10 = latticeSelector[j + by0];
        int b01 = latticeSelector[i + by1];
        int b11 = latticeSelector[j + by1];

        double sx = sCurve(rx0);
        double sy = sCurve(ry0);

        double[] q = gradient[channel][b00];
        double u = rx0 * q[0] + ry0 * q[1];
        q = gradient[channel][b10];
        double v = rx1 * q[0] + ry0 * q[1];
        double a = lerp(sx, u, v);

        q = gradient[channel][b01];
        u = rx0 * q[0] + ry1 * q[1];
        q = gradient[channel][b11];
        v = rx1 * q[0] + ry1 * q[1];
        double b = lerp(sx, u, v);

        return lerp(sy, a, b);
    }

    /**
     * The {@code turbFunctionResult} for one channel at one point - {@code fractalSum} sums {@code noise2()/ratio} across octaves (aimed at {@code [-1, 1]}), otherwise
     * {@code |noise2()|/ratio} (aimed at {@code [0, 1]}), doubling frequency (and, when stitching, the stitch state) each octave. {@code stitchInfo} is this call's own starting
     * stitch state - {@code null} when not stitching - mutated only in local copies here, since the caller reuses the same starting values for every pixel and channel.
     */
    double turbulence(int channel, double x, double y, double baseFreqX, double baseFreqY, int numOctaves, boolean fractalSum, StitchInfo stitchInfo) {
        boolean stitching = stitchInfo != null;
        int stitchWidth = 0;
        int stitchWrapX = 0;
        int stitchHeight = 0;
        int stitchWrapY = 0;
        if (stitching) {
            stitchWidth = stitchInfo.width();
            stitchWrapX = stitchInfo.wrapX();
            stitchHeight = stitchInfo.height();
            stitchWrapY = stitchInfo.wrapY();
        }

        double sum = 0.0;
        double vecX = x * baseFreqX;
        double vecY = y * baseFreqY;
        double ratio = 1.0;
        for (int octave = 0; octave < numOctaves; octave++) {
            StitchInfo current = stitching ? new StitchInfo(stitchWidth, stitchWrapX, stitchHeight, stitchWrapY) : null;
            double n = noise2(channel, vecX, vecY, current);
            sum += fractalSum ? n / ratio : Math.abs(n) / ratio;
            vecX *= 2;
            vecY *= 2;
            ratio *= 2;
            if (stitching) {
                stitchWidth *= 2;
                stitchWrapX = 2 * stitchWrapX - PERLIN_N;
                stitchHeight *= 2;
                stitchWrapY = 2 * stitchWrapY - PERLIN_N;
            }
        }
        return sum;
    }

}
