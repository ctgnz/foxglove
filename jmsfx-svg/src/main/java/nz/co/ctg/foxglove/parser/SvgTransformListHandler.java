package nz.co.ctg.foxglove.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;

import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

public class SvgTransformListHandler {
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private final List<Transform> transforms = new ArrayList<>();

    public List<Transform> parse(String transformText) {
        try {
            startTransformList();
            Splitter.on(WHITESPACE).split(transformText.trim()).forEach(val -> {
                if (StringUtils.isNotBlank(val)) {
                    String transformName = val.substring(0, val.indexOf('('));
                    String values = val.substring(val.indexOf('(') + 1, val.indexOf(')')).trim();
                    List<Double> numericValues = new ArrayList<>();
                    Splitter.on(',').split(values).forEach(numVal -> {
                        numericValues.add(Double.valueOf(numVal));
                    });
                    switch (transformName) {
                        case "matrix" :
                            matrix(numericValues.get(0), numericValues.get(1), numericValues.get(2), numericValues.get(3), numericValues.get(4), numericValues.get(5));
                            break;
                        case "translate" :
                            if (numericValues.size() > 1) {
                                translate(numericValues.get(0), numericValues.get(1));
                            } else {
                                translate(numericValues.get(0));
                            }
                            break;
                        case "scale" :
                            if (numericValues.size() > 1) {
                                scale(numericValues.get(0), numericValues.get(1));
                            } else {
                                scale(numericValues.get(0));
                            }
                            break;
                        case "rotate" :
                            if (numericValues.size() > 1) {
                                rotate(numericValues.get(0), numericValues.get(1), numericValues.get(2));
                            } else {
                                rotate(numericValues.get(0));
                            }
                            break;
                        case "skewX" :
                            skewX(numericValues.get(0));
                            break;
                        case "skewY" :
                            skewY(numericValues.get(0));
                            break;
                    }
                }
             });
            return transforms;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void translate(double tx, double ty) {
        transforms.add(new Translate(tx, ty));
    }

    public void translate(double tx) {
        transforms.add(new Translate(tx, 0));
    }

    public void startTransformList() {
        transforms.clear();
    }

    public void skewY(double sky) {
        transforms.add(new Shear(0, sky));
    }

    public void skewX(double skx) {
        transforms.add(new Shear(skx, 0));
    }

    public void scale(double sx, double sy) {
        transforms.add(new Scale(sx, sy));
    }

    public void scale(double sx) {
        transforms.add(new Scale(sx, sx));
    }

    public void rotate(double theta, double cx, double cy) {
        transforms.add(new Rotate(theta, cx, cy));
    }

    public void rotate(double theta) {
        transforms.add(new Rotate(theta));
    }

    public void matrix(double mxx, double myx, double mxy, double myy, double tx, double ty) {
        transforms.add(new Affine(mxx, mxy, tx, myx, myy, ty));
    }

    public void endTransformList() {
    }

}