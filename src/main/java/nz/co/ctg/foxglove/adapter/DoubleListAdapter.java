package nz.co.ctg.foxglove.adapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.StringUtils;

import static java.util.stream.Collectors.joining;

public class DoubleListAdapter extends XmlAdapter<String, List<Double>> {

    private static final String WHITESPACE_CHARS = ", \t\r\n";

    @Override
    public List<Double> unmarshal(String value) throws Exception {
        if (StringUtils.isBlank(value) || "none".equalsIgnoreCase(value)) {
            return Collections.emptyList();
        }
        return Arrays.stream(StringUtils.split(value, WHITESPACE_CHARS)).map(Double::valueOf).collect(Collectors.toList());
    }

    @Override
    public String marshal(List<Double> values) throws Exception {
        return values != null ? values.stream().map(val -> val.toString()).collect(joining(",")) : null;
    }

}
