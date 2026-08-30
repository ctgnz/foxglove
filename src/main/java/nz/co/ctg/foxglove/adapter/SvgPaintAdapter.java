package nz.co.ctg.foxglove.adapter;

import nz.co.ctg.foxglove.type.SvgPaint;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Binds {@code fill} and {@code stroke}.
 * <p>
 * The value is not always a paint that can be built while parsing - {@code url(#grad)} names an element that may not
 * have been read yet, and {@code currentColor} depends on where the value ends up being used - so it unmarshals to
 * an {@link SvgPaint}, which carries the unresolved cases through to render time. A blank value unmarshals to null,
 * leaving the property unspecified so that it inherits.
 */
public class SvgPaintAdapter extends XmlAdapter<String, SvgPaint> {

    @Override
    public SvgPaint unmarshal(String value) throws Exception {
        return SvgPaint.parse(value);
    }

    @Override
    public String marshal(SvgPaint value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.isColor() ? toString(value.getPaint()) : value.toString();
    }

    private String toString(Paint value) {
        if (value instanceof Color color) {
            if (color == Color.TRANSPARENT) {
                return "none";
            }
            int r = (int) (color.getRed() * 255);
            int g = (int) (color.getGreen() * 255);
            int b = (int) (color.getBlue() * 255);
            return String.format("#%02X%02X%02X", r, g, b);
        }
        return String.valueOf(value);
    }

}
