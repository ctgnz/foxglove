package nz.co.ctg.foxglove.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;

import javafx.geometry.Point2D;

public class PointListAdapter extends XmlAdapter<String, List<Point2D>> {

    @Override
    public List<Point2D> unmarshal(String value) throws Exception {
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        List<Point2D> points = new ArrayList<>();
        Iterable<String> split = Splitter.on(Pattern.compile("\\s")).split(value);
        split.forEach(val -> {
            if (StringUtils.isNotBlank(val)) {
                points.add(split(val));
            }
        });
        return points;
    }

    @Override
    public String marshal(List<Point2D> value) throws Exception {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return value.stream().map(pt -> String.format("%f,%f", pt.getX(), pt.getY())).collect(Collectors.joining(" "));
    }

    private Point2D split(String value) {
        String[] split = value.split(",");
        return new Point2D(Double.valueOf(split[0]), Double.valueOf(split[1]));
    }

}
