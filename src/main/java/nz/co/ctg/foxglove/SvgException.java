package nz.co.ctg.foxglove;

public final class SvgException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public final SvgExceptionCode code;

    public SvgException(SvgExceptionCode code, String message) {
        super(message);
        this.code = code;
    }

    public SvgExceptionCode getCode() {
        return code;
    }

}
