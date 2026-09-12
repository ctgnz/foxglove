package nz.co.ctg.foxglove;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.ImmutableMap;

import nz.co.ctg.foxglove.element.SvgCursor;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

/**
 * Resolves the {@code cursor} presentation attribute to a JavaFX {@link Cursor}. Never rendered/applied by
 * {@code SvgCursor} itself - referenced only via {@code cursor}, the same "referenced, not rendered" shape as
 * {@code <marker>}.
 */
public final class SvgCursorResolver {

    private static final Map<String, Cursor> KEYWORDS = ImmutableMap.<String, Cursor>builder()
        .put("pointer", Cursor.HAND)
        .put("crosshair", Cursor.CROSSHAIR)
        .put("move", Cursor.MOVE)
        .put("text", Cursor.TEXT)
        .put("wait", Cursor.WAIT)
        .put("n-resize", Cursor.N_RESIZE)
        .put("ne-resize", Cursor.NE_RESIZE)
        .put("nw-resize", Cursor.NW_RESIZE)
        .put("e-resize", Cursor.E_RESIZE)
        .put("se-resize", Cursor.SE_RESIZE)
        .put("sw-resize", Cursor.SW_RESIZE)
        .put("s-resize", Cursor.S_RESIZE)
        .put("w-resize", Cursor.W_RESIZE)
        .build();

    /**
     * Resolves a {@code cursor} value - a comma-separated list of zero or more {@code url(#id)} references followed
     * by a standard CSS2 keyword fallback, per the specification - to the {@link Cursor} it names, or {@code null}
     * when {@code rawValue} is blank or nothing in it resolves (the caller then leaves the node's cursor untouched,
     * rather than forcing a default, so JavaFX's own inheritance from an ancestor node still applies).
     * <p>
     * A {@code url(#id)} resolves through {@code index} to an {@code SvgCursor} and loads its {@code xlink:href} as
     * an {@link Image} - only a {@code data:} URI is supported, the same scope already established for
     * {@code <image>} (#20) - falling through to the next comma-separated token were it not resolvable, matching
     * the CSS fallback semantics {@code cursor} itself defines. A recognised keyword maps to its {@link Cursor}
     * constant; {@code auto}/{@code default}/anything unrecognised maps to {@link Cursor#DEFAULT}.
     */
    public static Cursor resolve(String rawValue, SvgElementIndex index) {
        if (StringUtils.isBlank(rawValue)) {
            return null;
        }
        String[] tokens = rawValue.split(",");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            boolean lastToken = i == tokens.length - 1;
            if (token.toLowerCase(Locale.ROOT).startsWith("url(")) {
                Cursor imageCursor = resolveImageCursor(token, index);
                if (imageCursor != null) {
                    return imageCursor;
                }
                continue;
            }
            if (lastToken) {
                return KEYWORDS.getOrDefault(token.toLowerCase(Locale.ROOT), Cursor.DEFAULT);
            }
        }
        return Cursor.DEFAULT;
    }

    private static Cursor resolveImageCursor(String urlToken, SvgElementIndex index) {
        return index.resolve(urlToken, SvgCursor.class)
            .map(cursor -> {
                String href = cursor.getXlinkHref();
                if (StringUtils.isBlank(href) || !href.startsWith("data:")) {
                    return null;
                }
                try {
                    Image image = new Image(href);
                    if (image.isError()) {
                        return null;
                    }
                    double hotspotX = parseCoordinate(cursor.getX());
                    double hotspotY = parseCoordinate(cursor.getY());
                    return (Cursor) new ImageCursor(image, hotspotX, hotspotY);
                } catch (Exception e) {
                    return null;
                }
            })
            .orElse(null);
    }

    private static double parseCoordinate(String value) {
        if (StringUtils.isBlank(value) || !NumberUtils.isParsable(value.trim())) {
            return 0;
        }
        return NumberUtils.toDouble(value.trim(), 0);
    }

    private SvgCursorResolver() {
    }

}
