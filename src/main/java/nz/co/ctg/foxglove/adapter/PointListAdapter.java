package nz.co.ctg.foxglove.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Parses a {@code <polygon>}/{@code <polyline>} {@code points} attribute - a flat "list of numbers" (SVG's own grammar term), not a list of comma-joined "x,y" pairs. Numbers may
 * be separated by whitespace, a comma, or nothing at all when a sign or a new decimal point unambiguously starts the next number ({@code "179-185"} is two numbers, {@code 179} and
 * {@code -185}) - the exact same tokenization {@link nz.co.ctg.foxglove.geometry.SvgPathData} already relies on for path data's own number lists, for the same reason. An odd
 * trailing number with no pair partner is dropped, per the spec.
 * <p>
 * The previous implementation split on whitespace only, then required each resulting token to itself contain exactly one comma joining two numbers - real documents don't reliably
 * write points that way (space-only pairs, a stray space after a comma, every number comma-joined with no whitespace at all, or a mix of all three within one list), so it silently
 * dropped points or threw depending on which of those forms it hit (issue #94).
 */
public class PointListAdapter extends XmlAdapter<String, List<Point2D>> {

    private static final Pattern NUMBER = Pattern.compile("[-+]?(?:\\d+\\.\\d*|\\.\\d+|\\d+)(?:[eE][-+]?\\d+)?");

    @Override
    public List<Point2D> unmarshal(String value) throws Exception {
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        List<Double> numbers = new ArrayList<>();
        Matcher matcher = NUMBER.matcher(value);
        while (matcher.find()) {
            numbers.add(Double.valueOf(matcher.group()));
        }
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i + 1 < numbers.size(); i += 2) {
            points.add(new Point2D(numbers.get(i), numbers.get(i + 1)));
        }
        return points;
    }

    @Override
    public String marshal(List<Point2D> value) throws Exception {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return value.stream()
            .map(pt -> String.format("%f,%f", pt.getX(), pt.getY()))
            .collect(Collectors.joining(" "));
    }

}
