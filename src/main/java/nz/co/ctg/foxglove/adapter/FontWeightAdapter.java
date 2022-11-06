package nz.co.ctg.foxglove.adapter;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.scene.text.FontWeight;

public class FontWeightAdapter extends XmlAdapter<String, FontWeight> {

    @Override
    public FontWeight unmarshal(String weight) throws Exception {
        if (StringUtils.isNumeric(weight)) {
            return FontWeight.findByWeight(Integer.parseInt(weight));
        }
        if (StringUtils.isNotBlank(weight)) {
            switch (weight.toLowerCase()) {
                case "lighter":
                    return FontWeight.LIGHT;
                case "normal":
                    return FontWeight.NORMAL;
                case "bold":
                    return FontWeight.BOLD;
                case "bolder":
                    return FontWeight.EXTRA_BOLD;
            }
        }
        return FontWeight.NORMAL;
    }

    @Override
    public String marshal(FontWeight weight) throws Exception {
        return Integer.toString(weight.getWeight());
    }

}
