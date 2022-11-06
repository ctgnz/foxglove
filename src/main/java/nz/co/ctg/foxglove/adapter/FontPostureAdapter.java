package nz.co.ctg.foxglove.adapter;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.scene.text.FontPosture;

public class FontPostureAdapter extends XmlAdapter<String, FontPosture> {

    @Override
    public FontPosture unmarshal(String value) throws Exception {
        if (StringUtils.isNotBlank(value)) {
            switch (value.toLowerCase()) {
                case "italic": // fall through
                case "oblique":
                    return FontPosture.ITALIC;
                case "normal":
                    return FontPosture.REGULAR;
            }
        }
        return FontPosture.REGULAR;
    }

    @Override
    public String marshal(FontPosture value) throws Exception {
        return value == FontPosture.ITALIC ? "italic" : "normal";
    }

}
