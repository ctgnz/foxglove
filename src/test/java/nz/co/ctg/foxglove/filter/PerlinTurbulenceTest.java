package nz.co.ctg.foxglove.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.jupiter.api.Test;

/**
 * Exercises #171's acceptance criteria: {@link PerlinTurbulence} matches known-correct reference values rather than only "doesn't throw" - computed independently, by a separate
 * Python transcription of the same SVG 1.1 15.24 algorithm (not just re-reading this class's own source), for a handful of fixed {@code (seed, x, y, baseFrequency, numOctaves)}
 * cases. {@code (2.5, 2.5)} and {@code (13, 7)} were chosen deliberately off any exact lattice point - Perlin noise is exactly {@code 0} at every integer-aligned lattice
 * coordinate for any seed, so a test anchored there could not actually distinguish a correct gradient/permutation table from a broken one.
 * <p>
 * Also covers the {@code seed} attribute's truncate-toward-zero rule (SVG 1.1 15.24: "must first be truncated... towards zero") - {@code filters-turb-02-f} exercises exactly this
 * with a run of fractional and negative seeds.
 */
public class PerlinTurbulenceTest {

    @Test
    public void testTurbulenceMatchesTheIndependentReferenceComputation() {
        PerlinTurbulence generator = new PerlinTurbulence(0);
        double result = generator.turbulence(0, 2.5, 2.5, 0.1, 0.1, 1, false, null);
        assertThat(result, closeTo(0.18872521420376834, 1e-9));
    }

    @Test
    public void testFractalNoiseAcrossTwoOctavesMatchesTheIndependentReferenceComputation() {
        PerlinTurbulence generator = new PerlinTurbulence(0);
        double result = generator.turbulence(3, 13.0, 7.0, 0.1, 0.1, 2, true, null);
        assertThat(result, closeTo(-0.2754662588430778, 1e-9));
    }

    @Test
    public void testSeedTruncatesTowardZeroSoAFractionalSeedMatchesItsTruncatedInteger() {
        // -0.8 truncates to 0 (toward zero, per spec), not -1 (floor) - filters-turb-02-f exercises exactly this
        PerlinTurbulence zero = new PerlinTurbulence(0);
        PerlinTurbulence fractional = new PerlinTurbulence(-0.8);
        double expected = zero.turbulence(0, 2.5, 2.5, 0.1, 0.1, 1, false, null);
        double actual = fractional.turbulence(0, 2.5, 2.5, 0.1, 0.1, 1, false, null);
        assertThat(actual, closeTo(expected, 1e-12));
    }

    @Test
    public void testADifferentIntegerSeedProducesDifferentNoise() {
        PerlinTurbulence zero = new PerlinTurbulence(0);
        PerlinTurbulence minusOne = new PerlinTurbulence(-1);
        double a = zero.turbulence(0, 2.5, 2.5, 0.1, 0.1, 1, false, null);
        double b = minusOne.turbulence(0, 2.5, 2.5, 0.1, 0.1, 1, false, null);
        assertThat(Math.abs(a - b) > 0.01, is(true));
    }

}
