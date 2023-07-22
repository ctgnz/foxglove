package nz.co.ctg.foxglove.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;

import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

public class SvgTransformListAdapter {
    private static final Pattern WHITESPACE = Pattern.compile("[,\\s]");

    public List<Transform> parse(String transformText) {
        try {
            List<Transform> transforms = new ArrayList<>();
            Splitter.on(")").split(transformText).forEach(transform -> {
                if (StringUtils.isNotBlank(transform)) {
                    String transformName = transform.substring(0, transform.indexOf('(')).trim();
                    String values = transform.substring(transform.indexOf('(') + 1).trim();
                    List<Double> numericValues = new ArrayList<>();
                    Splitter.on(WHITESPACE).split(values).forEach(numVal -> {
                        if (StringUtils.isNotBlank(numVal)) {
                            numericValues.add(Double.valueOf(numVal));
                        }
                    });
                    switch (transformName) {
                        case "matrix" :
                            transforms.add(new Affine(numericValues.get(0), numericValues.get(2), numericValues.get(4), numericValues.get(1), numericValues.get(3), numericValues.get(5)));
                            break;
                        case "translate" :
                            if (numericValues.size() > 1) {
                                transforms.add(new Translate(numericValues.get(0), numericValues.get(1)));
                            } else {
                                transforms.add(new Translate(numericValues.get(0), 0));
                            }
                            break;
                        case "scale" :
                            if (numericValues.size() > 1) {
                                transforms.add(new Scale(numericValues.get(0), numericValues.get(1)));
                            } else {
                                double sx = numericValues.get(0);
                                transforms.add(new Scale(sx, sx));
                            }
                            break;
                        case "rotate" :
                            if (numericValues.size() > 1) {
                                transforms.add(new Rotate(numericValues.get(0), numericValues.get(1), numericValues.get(2)));
                            } else {
                                transforms.add(new Rotate(numericValues.get(0)));
                            }
                            break;
                        case "skewX" :
                            transforms.add(new Shear(tan(toRadians(numericValues.get(0))), 0));
                            break;
                        case "skewY" :
                            transforms.add(new Shear(0, tan(toRadians(numericValues.get(0)))));
                            break;
                    }
                }
            });
            return transforms;
        } catch (Exception e) {
            System.out.format("Transform parse error [%s]%n", transformText);
            return Collections.emptyList();
        }
    }

}