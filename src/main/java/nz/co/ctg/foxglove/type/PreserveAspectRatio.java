package nz.co.ctg.foxglove.type;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * A parsed {@code preserveAspectRatio} attribute value, controlling how a {@code viewBox} is fitted into its
 * viewport.
 */
public final class PreserveAspectRatio {

    public enum Align {
        NONE(0, 0),
        X_MIN_Y_MIN(0, 0),
        X_MID_Y_MIN(0.5, 0),
        X_MAX_Y_MIN(1, 0),
        X_MIN_Y_MID(0, 0.5),
        X_MID_Y_MID(0.5, 0.5),
        X_MAX_Y_MID(1, 0.5),
        X_MIN_Y_MAX(0, 1),
        X_MID_Y_MAX(0.5, 1),
        X_MAX_Y_MAX(1, 1);

        private final double alignX;
        private final double alignY;

        Align(double alignX, double alignY) {
            this.alignX = alignX;
            this.alignY = alignY;
        }

        public double getAlignX() {
            return alignX;
        }

        public double getAlignY() {
            return alignY;
        }
    }

    public enum MeetOrSlice {
        MEET,
        SLICE
    }

    private static final PreserveAspectRatio DEFAULT = new PreserveAspectRatio(Align.X_MID_Y_MID, MeetOrSlice.MEET);

    /**
     * Parses a {@code preserveAspectRatio} attribute value, tolerating a leading {@code defer} token (meaningless
     * outside of {@code <image>} references and otherwise ignored) and falling back to the default -
     * {@code xMidYMid meet} - when {@code raw} is blank or unrecognised.
     */
    public static PreserveAspectRatio parse(String raw) {
        if (StringUtils.isBlank(raw)) {
            return DEFAULT;
        }
        String[] tokens = raw.trim().split("\\s+");
        int index = 0;
        if (index < tokens.length && "defer".equals(tokens[index])) {
            index++;
        }
        Align align = index < tokens.length ? parseAlign(tokens[index]) : null;
        if (align == null) {
            return DEFAULT;
        }
        index++;
        MeetOrSlice meetOrSlice = index < tokens.length ? parseMeetOrSlice(tokens[index]) : MeetOrSlice.MEET;
        return new PreserveAspectRatio(align, meetOrSlice);
    }

    private static Align parseAlign(String token) {
        switch (token) {
            case "none":
                return Align.NONE;
            case "xMinYMin":
                return Align.X_MIN_Y_MIN;
            case "xMidYMin":
                return Align.X_MID_Y_MIN;
            case "xMaxYMin":
                return Align.X_MAX_Y_MIN;
            case "xMinYMid":
                return Align.X_MIN_Y_MID;
            case "xMidYMid":
                return Align.X_MID_Y_MID;
            case "xMaxYMid":
                return Align.X_MAX_Y_MID;
            case "xMinYMax":
                return Align.X_MIN_Y_MAX;
            case "xMidYMax":
                return Align.X_MID_Y_MAX;
            case "xMaxYMax":
                return Align.X_MAX_Y_MAX;
            default:
                return null;
        }
    }

    private static MeetOrSlice parseMeetOrSlice(String token) {
        return "slice".equals(token) ? MeetOrSlice.SLICE : MeetOrSlice.MEET;
    }

    private final Align align;
    private final MeetOrSlice meetOrSlice;

    private PreserveAspectRatio(Align align, MeetOrSlice meetOrSlice) {
        this.align = align;
        this.meetOrSlice = meetOrSlice;
    }

    public Align getAlign() {
        return align;
    }

    public MeetOrSlice getMeetOrSlice() {
        return meetOrSlice;
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper("preserveAspectRatio");
        builder.add("align", align);
        builder.add("meetOrSlice", meetOrSlice);
        return builder.toString();
    }

}
