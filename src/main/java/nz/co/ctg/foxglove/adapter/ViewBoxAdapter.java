package nz.co.ctg.foxglove.adapter;

import nz.co.ctg.foxglove.type.ViewBox;

import static nz.co.ctg.foxglove.adapter.AdapterConstants.WHITESPACE_CHARS;
import static org.apache.commons.lang3.StringUtils.split;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.css.Size;

public class ViewBoxAdapter extends XmlAdapter<String, ViewBox> {
    private final SizeAdapter adapter = new SizeAdapter();

    @Override
    public ViewBox unmarshal(String value) throws Exception {
        String[] components = split(value, WHITESPACE_CHARS);
        if (components.length == 4) {
            Size x = adapter.unmarshal(components[0]);
            Size y = adapter.unmarshal(components[1]);
            Size width = adapter.unmarshal(components[2]);
            Size height = adapter.unmarshal(components[3]);
            return new ViewBox(x, y, width, height);
        }
        return null;
    }

    @Override
    public String marshal(ViewBox value) throws Exception {
        return String.format("%s %s %s %s", adapter.marshal(value.getMinX()), adapter.marshal(value.getMinY()), adapter.marshal(value.getWidth()), adapter.marshal(value.getHeight()));
    }

}
