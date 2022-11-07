package nz.co.ctg.foxglove.adapter;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Maps;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.css.Size;
import javafx.css.SizeUnits;

public class SizeAdapter extends XmlAdapter<String, Size> {
    // The toString() method in SizeUnits is used for the XML representation, so can be used to make a reverse lookup map of the SizeUnits values
    private static final Map<String, SizeUnits> SIZE_UNITS = Maps.uniqueIndex(Arrays.asList(SizeUnits.values()), SizeUnits::toString);

    @Override
    public Size unmarshal(String value) throws Exception {
        if (NumberUtils.isParsable(value)) {
            return new Size(NumberUtils.toDouble(value), SizeUnits.PX);
        } else {
            // if the string value is not parsable as a number, it must contain size units,
            // which will be the last component returned by splitByCharacterType
            String[] parts = StringUtils.splitByCharacterType(value);
            String unitPart = parts[parts.length - 1];
            String numberPart = StringUtils.substring(value, 0, value.indexOf(unitPart));
            return new Size(NumberUtils.toDouble(numberPart), SIZE_UNITS.getOrDefault(unitPart, SizeUnits.PX));
        }
    }

    @Override
    public String marshal(Size value) throws Exception {
        return String.format("%s%s", value.getValue(), value.getUnits());
    }

}
