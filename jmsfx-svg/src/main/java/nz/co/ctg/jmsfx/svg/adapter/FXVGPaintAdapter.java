package nz.co.ctg.jmsfx.svg.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import javafx.scene.paint.Paint;

public class FXVGPaintAdapter extends XmlAdapter<String, Paint> {

    @Override
    public Paint unmarshal(String value) throws Exception {
        return Paint.valueOf(value);
    }

    @Override
    public String marshal(Paint value) throws Exception {
        return value.toString();
    }

}
