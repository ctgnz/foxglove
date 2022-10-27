package nz.co.ctg.foxglove.parser;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import javafx.scene.paint.Paint;

public class SvgPaintAdapter extends XmlAdapter<String, Paint> {

    @Override
    public Paint unmarshal(String value) throws Exception {
        if ("none".equals(value)) {
            return null;
        }
        return Paint.valueOf(value);
    }

    @Override
    public String marshal(Paint value) throws Exception {
        return value.toString();
    }

}
