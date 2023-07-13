package nz.co.ctg.foxglove.adapter;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class SvgPaintAdapter extends XmlAdapter<String, Paint> {

    @Override
    public Paint unmarshal(String value) throws Exception {
        if (StringUtils.isBlank(value)) {
            return Color.BLACK;
        }
        if ("none".equals(value)) {
            return Color.TRANSPARENT;
        }
        return Paint.valueOf(value);
    }

    @Override
    public String marshal(Paint value) throws Exception {
        return value != null ? toString(value) : toString(Color.BLACK);
    }

    private String toString(Paint value) {
        if (value instanceof Color) {
            Color color = (Color) value;
            if (color == Color.TRANSPARENT) {
                return "none";
            }
            int r = (int) (color.getRed() * 255);
            int g = (int) (color.getGreen() * 255);
            int b = (int) (color.getBlue() * 255);
            int a = (int) (color.getOpacity() * 255);
            return String.format("#%02X%02X%02X%02X", r, g, b, a);
        }
        return value.toString();
    }

}
