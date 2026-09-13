package nz.co.ctg.foxglove.filter;

/**
 * Thrown internally to abort building a filter the moment any primitive, or any {@code in}/{@code in2} reference,
 * falls outside what a given path supports - caught once, at the top, so every abort path degrades identically
 * without each call site needing its own early-return plumbing.
 * <p>
 * Aborting the {@link SvgFilterRenderer} effect-chain path is not the end of the road: it is what hands the filter
 * to {@link SvgFilterRasterPipeline} instead. Aborting there too is a genuine degrade - the element renders
 * unfiltered.
 */
final class UnsupportedFilterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    UnsupportedFilterException() {
        super(null, null, false, false);
    }

}
