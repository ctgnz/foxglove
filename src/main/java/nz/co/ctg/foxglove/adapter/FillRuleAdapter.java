package nz.co.ctg.foxglove.adapter;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.scene.shape.FillRule;

public class FillRuleAdapter extends XmlAdapter<String, FillRule> {

    @Override
    public FillRule unmarshal(String value) throws Exception {
        if (StringUtils.isBlank(value)) {
            switch (value) {
                case "evenodd":
                    return FillRule.EVEN_ODD;
                default:
                    return FillRule.NON_ZERO;
            }
        }
        return null;
    }

    @Override
    public String marshal(FillRule value) throws Exception {
        if (value != null) {
            switch (value) {
                case EVEN_ODD:
                    return "evenodd";
                default:
                    return "nonzero";
            }
        }
        return null;
    }

}
