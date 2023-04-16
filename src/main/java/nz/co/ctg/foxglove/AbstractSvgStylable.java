package nz.co.ctg.foxglove;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.adapter.DoubleListAdapter;
import nz.co.ctg.foxglove.adapter.FontPostureAdapter;
import nz.co.ctg.foxglove.adapter.FontWeightAdapter;
import nz.co.ctg.foxglove.adapter.StrokeLineCapAdapter;
import nz.co.ctg.foxglove.adapter.StrokeLineJoinAdapter;
import nz.co.ctg.foxglove.adapter.SvgPaintAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractSvgStylable extends AbstractSvgElement implements ISvgStylable {
    private static final SvgPaintAdapter paintAdapter = new SvgPaintAdapter();
    private static final StrokeLineCapAdapter strokeLineCapAdapter = new StrokeLineCapAdapter();
    private static final StrokeLineJoinAdapter strokeLineJoinAdapter = new StrokeLineJoinAdapter();
    private static final DoubleListAdapter doubleListAdapter = new DoubleListAdapter();
    private static final FontWeightAdapter fontWeightAdapter = new FontWeightAdapter();
    private static final FontPostureAdapter fontPostureAdapter = new FontPostureAdapter();

    public AbstractSvgStylable() {
    }

    public boolean isVisible() {
        String display = getDisplay();
        return display == null || StringUtils.equalsIgnoreCase("none", display);
    }

    public void parseStyle() {
        if (StringUtils.isNotBlank(getStyle())) {
            Arrays.stream(StringUtils.split(getStyle(), ';')).forEach(stylePair -> {
                String[] values = StringUtils.split(stylePair, ':');
                String name = values[0].toLowerCase().trim();
                String value = values[1].toLowerCase().trim();
                switch (name) {
                    case TEXT_FONT_FAMILY:
                        setFontFamily(value);
                        break;
                    case TEXT_FONT_SIZE:
                        setFontSize(value);
                        break;
                    case TEXT_FONT_STYLE:
                        setFontStyle(parseFontPosture(value));
                        break;
                    case TEXT_FONT_WEIGHT:
                        setFontWeight(parseFontWeight(value));
                        break;
                    case GRAPHX_FILL:
                        setFill(parsePaint(value));
                        break;
                    case GRAPHX_STROKE:
                        setStroke(parsePaint(value));
                        break;
                    case GRAPHX_STROKE_WIDTH:
                        setStrokeWidth(parseDouble(value));
                        break;
                    case GRAPHX_STROKE_LINECAP:
                        setStrokeLineCap(parseStrokeLineCap(value));
                        break;
                    case GRAPHX_STROKE_LINEJOIN:
                        setStrokeLineJoin(parseStrokeLineJoin(value));
                        break;
                    case GRAPHX_STROKE_MITERLIMIT:
                        setStrokeMiterLimit(parseDouble(value));
                        break;
                    case GRAPHX_STROKE_DASHOFFSET:
                        setStrokeDashOffset(parseDouble(value));
                        break;
                    case GRAPHX_STROKE_DASHARRAY:
                        setStrokeDashArray(parseDoubleList(value));
                        break;
                }
            });
        }
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        ISvgStylable.super.toStringDetail(builder);
    }

    private Paint parsePaint(String value) {
        try {
            return paintAdapter.unmarshal(value);
        } catch (Exception e) {
            return null;
        }
    }

    private StrokeLineCap parseStrokeLineCap(String value) {
        try {
            return strokeLineCapAdapter.unmarshal(value);
        } catch (Exception e) {
            return null;
        }
    }

    private StrokeLineJoin parseStrokeLineJoin(String value) {
        try {
            return strokeLineJoinAdapter.unmarshal(value);
        } catch (Exception e) {
            return null;
        }
    }

    private FontWeight parseFontWeight(String value) {
        try {
            return fontWeightAdapter.unmarshal(value);
        } catch (Exception e) {
            return null;
        }
    }

    private FontPosture parseFontPosture(String value) {
        try {
            return fontPostureAdapter.unmarshal(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private List<Double> parseDoubleList(String value) {
        try {
            return doubleListAdapter.unmarshal(value);
        } catch (Exception e) {
            return null;
        }
    }

}