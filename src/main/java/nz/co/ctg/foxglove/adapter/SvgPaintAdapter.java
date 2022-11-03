package nz.co.ctg.foxglove.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.paint.Paint;

public class SvgPaintAdapter extends XmlAdapter<String, Paint> {

    @Override
    public Paint unmarshal(String value) throws Exception {
        if (StringUtils.isBlank(value) || "none".equals(value)) {
            return null;
        }
        return Paint.valueOf(value);
    }

    @Override
    public String marshal(Paint value) throws Exception {
        return value != null ? value.toString() : null;
    }

}
