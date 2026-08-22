package nz.co.ctg.foxglove.adapter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Maps;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.css.Size;
import javafx.css.SizeUnits;

public class SizeAdapter extends XmlAdapter<String, Size> {
    private static final SizeAdapter INST = new SizeAdapter();
    // The toString() method in SizeUnits is used for the XML representation, so can be used to make a reverse lookup map of the SizeUnits values
    private static final Map<String, SizeUnits> SIZE_UNITS = Maps.uniqueIndex(Arrays.asList(SizeUnits.values()), SizeUnits::toString);

    public static Size parse(String value) {
        try {
            return INST.unmarshal(value);
        } catch (Exception e) {
            return new Size(0, SizeUnits.PX);
        }
    }

    @Override
    public String marshal(Size value) throws Exception {
        if (value.getUnits() == SizeUnits.PX) {
            return Double.toString(value.getValue());
        } else {
            return String.format("%s%s", Double.toString(value.getValue()), value.getUnits());
        }
    }

    @Override
    public Size unmarshal(String value) throws Exception {
        if (NumberUtils.isParsable(value)) {
            return new Size(NumberUtils.toDouble(value), SizeUnits.PX);
        } else {
            // if the string value is not parsable as a number, it must contain size units, which are the
            // trailing run of unit characters
            int unitStart = indexOfUnits(value);
            String numberPart = StringUtils.substring(value, 0, unitStart);
            // SIZE_UNITS is keyed on the lower case SizeUnits.toString(), but unit names are case insensitive
            String unitPart = StringUtils.substring(value, unitStart).toLowerCase(Locale.ROOT);
            return new Size(NumberUtils.toDouble(numberPart), SIZE_UNITS.getOrDefault(unitPart, SizeUnits.PX));
        }
    }

    private static int indexOfUnits(String value) {
        int index = value.length();
        while (index > 0 && isUnitCharacter(value.charAt(index - 1))) {
            index--;
        }
        return index;
    }

    private static boolean isUnitCharacter(char character) {
        return Character.isLetter(character) || character == '%';
    }
}
