package nz.co.ctg.foxglove.attributes;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.parser.DoubleListAdapter;
import nz.co.ctg.foxglove.parser.StrokeLineCapAdapter;
import nz.co.ctg.foxglove.parser.StrokeLineJoinAdapter;
import nz.co.ctg.foxglove.parser.SvgPaintAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgGraphicsStyleAttributes implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_FILL = "fill";
    private static final String ATTR_FILL_RULE = "fill-rule";
    private static final String ATTR_STROKE = "stroke";
    private static final String ATTR_STROKE_DASHARRAY = "stroke-dasharray";
    private static final String ATTR_STROKE_DASHOFFSET = "stroke-dashoffset";
    private static final String ATTR_STROKE_LINECAP = "stroke-linecap";
    private static final String ATTR_STROKE_LINEJOIN = "stroke-linejoin";
    private static final String ATTR_STROKE_MITERLIMIT = "stroke-miterlimit";
    private static final String ATTR_STROKE_WIDTH = "stroke-width";
    private static final String ATTR_COLOR = "color";
    private static final String ATTR_COLOR_INTERPOLATION = "color-interpolation";
    private static final String ATTR_COLOR_RENDERING = "color-rendering";
    private static final String ATTR_OPACITY = "opacity";
    private static final String ATTR_FILL_OPACITY = "fill-opacity";
    private static final String ATTR_STROKE_OPACITY = "stroke-opacity";
    private static final String ATTR_DISPLAY = "display";
    private static final String ATTR_IMAGE_RENDERING = "image-rendering";
    private static final String ATTR_POINTER_EVENTS = "pointer-events";
    private static final String ATTR_SHAPE_RENDERING = "shape-rendering";
    private static final String ATTR_TEXT_RENDERING = "text-rendering";
    private static final String ATTR_VISIBILITY = "visibility";
    private static final String ATTR_COLOR_PROFILE = "color-profile";
    private static final String ATTR_STOP_COLOR = "stop-color";
    private static final String ATTR_STOP_OPACITY = "stop-opacity";

    @XmlAttribute(name = ATTR_FILL)
    @XmlJavaTypeAdapter(SvgPaintAdapter.class)
    private Paint fill;

    @XmlAttribute(name = ATTR_FILL_RULE)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String fillRule;

    @XmlAttribute(name = ATTR_STROKE)
    @XmlJavaTypeAdapter(SvgPaintAdapter.class)
    private Paint stroke;

    @XmlAttribute(name = ATTR_STROKE_DASHARRAY)
    @XmlJavaTypeAdapter(DoubleListAdapter.class)
    private List<Double> strokeDashArray;

    @XmlAttribute(name = ATTR_STROKE_DASHOFFSET)
    private double strokeDashOffset;

    @XmlAttribute(name = ATTR_STROKE_LINECAP)
    @XmlJavaTypeAdapter(StrokeLineCapAdapter.class)
    private StrokeLineCap strokeLineCap;

    @XmlAttribute(name = ATTR_STROKE_LINEJOIN)
    @XmlJavaTypeAdapter(StrokeLineJoinAdapter.class)
    private StrokeLineJoin strokeLineJoin;

    @XmlAttribute(name = ATTR_STROKE_MITERLIMIT)
    private double strokeMiterLimit;

    @XmlAttribute(name = ATTR_STROKE_WIDTH)
    private double strokeWidth;

    @XmlAttribute(name = ATTR_COLOR)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String color;

    @XmlAttribute(name = ATTR_COLOR_INTERPOLATION)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String colorInterpolation;

    @XmlAttribute(name = ATTR_COLOR_RENDERING)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String colorRendering;

    @XmlAttribute(name = ATTR_OPACITY)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String opacity;

    @XmlAttribute(name = ATTR_FILL_OPACITY)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String fillOpacity;

    @XmlAttribute(name = ATTR_STROKE_OPACITY)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String strokeOpacity;

    @XmlAttribute(name = ATTR_DISPLAY)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String display;

    @XmlAttribute(name = ATTR_IMAGE_RENDERING)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String imageRendering;

    @XmlAttribute(name = ATTR_POINTER_EVENTS)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String pointerEvents;

    @XmlAttribute(name = ATTR_SHAPE_RENDERING)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String shapeRendering;

    @XmlAttribute(name = ATTR_TEXT_RENDERING)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String textRendering;

    @XmlAttribute(name = ATTR_VISIBILITY)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String visibility;

    @XmlAttribute(name = ATTR_COLOR_PROFILE)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String colorProfile;

    @XmlAttribute(name = ATTR_STOP_COLOR)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String stopColor;

    @XmlAttribute(name = ATTR_STOP_OPACITY)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String stopOpacity;

    private AbstractTransformationMapping mapping;
    private static final SvgPaintAdapter paintAdapter = new SvgPaintAdapter();
    private static final StrokeLineCapAdapter strokeLineCapAdapter = new StrokeLineCapAdapter();
    private static final StrokeLineJoinAdapter strokeLineJoinAdapter = new StrokeLineJoinAdapter();
    private static final DoubleListAdapter doubleListAdapter = new DoubleListAdapter();

    public SvgGraphicsStyleAttributes() {
    }

    public Paint getFill() {
        return fill;
    }

    public void setFill(Paint fill) {
        this.fill = fill;
    }

    public String getFillRule() {
        return fillRule;
    }

    public void setFillRule(String fillRule) {
        this.fillRule = fillRule;
    }

    public Paint getStroke() {
        return stroke;
    }

    public void setStroke(Paint stroke) {
        this.stroke = stroke;
    }

    public List<Double> getStrokeDashArray() {
        return strokeDashArray;
    }

    public void setStrokeDashArray(List<Double> strokeDashArray) {
        this.strokeDashArray = strokeDashArray;
    }

    public double getStrokeDashOffset() {
        return strokeDashOffset;
    }

    public void setStrokeDashOffset(double strokeDashOffset) {
        this.strokeDashOffset = strokeDashOffset;
    }

    public StrokeLineCap getStrokeLineCap() {
        return strokeLineCap;
    }

    public void setStrokeLineCap(StrokeLineCap strokeLineCap) {
        this.strokeLineCap = strokeLineCap;
    }

    public StrokeLineJoin getStrokeLineJoin() {
        return strokeLineJoin;
    }

    public void setStrokeLineJoin(StrokeLineJoin strokeLineJoin) {
        this.strokeLineJoin = strokeLineJoin;
    }

    public double getStrokeMiterLimit() {
        return strokeMiterLimit;
    }

    public void setStrokeMiterLimit(double strokeMiterLimit) {
        this.strokeMiterLimit = strokeMiterLimit;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorInterpolation() {
        return colorInterpolation;
    }

    public void setColorInterpolation(String colorInterpolation) {
        this.colorInterpolation = colorInterpolation;
    }

    public String getColorRendering() {
        return colorRendering;
    }

    public void setColorRendering(String colorRendering) {
        this.colorRendering = colorRendering;
    }

    public String getOpacity() {
        return opacity;
    }

    public void setOpacity(String opacity) {
        this.opacity = opacity;
    }

    public String getFillOpacity() {
        return fillOpacity;
    }

    public void setFillOpacity(String fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    public String getStrokeOpacity() {
        return strokeOpacity;
    }

    public void setStrokeOpacity(String strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getImageRendering() {
        return imageRendering;
    }

    public void setImageRendering(String imageRendering) {
        this.imageRendering = imageRendering;
    }

    public String getPointerEvents() {
        return pointerEvents;
    }

    public void setPointerEvents(String pointerEvents) {
        this.pointerEvents = pointerEvents;
    }

    public String getShapeRendering() {
        return shapeRendering;
    }

    public void setShapeRendering(String shapeRendering) {
        this.shapeRendering = shapeRendering;
    }

    public String getTextRendering() {
        return textRendering;
    }

    public void setTextRendering(String textRendering) {
        this.textRendering = textRendering;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getColorProfile() {
        return colorProfile;
    }

    public void setColorProfile(String colorProfile) {
        this.colorProfile = colorProfile;
    }

    public String getStopColor() {
        return stopColor;
    }

    public void setStopColor(String stopColor) {
        this.stopColor = stopColor;
    }

    public String getStopOpacity() {
        return stopOpacity;
    }

    public void setStopOpacity(String stopOpacity) {
        this.stopOpacity = stopOpacity;
    }

    public void applyGraphicsProperties(Shape shape) {
        shape.setFill(fill);
        shape.setStroke(stroke);
        shape.setStrokeWidth(strokeWidth);
        shape.setStrokeMiterLimit(strokeMiterLimit);
        shape.setStrokeLineCap(strokeLineCap);
        shape.setStrokeLineJoin(strokeLineJoin);
        shape.setStrokeDashOffset(strokeDashOffset);
        if (strokeDashArray != null) {
            shape.getStrokeDashArray().addAll(strokeDashArray);
        }
    }

    public void parseStyle(String style) {
        if (StringUtils.isNotBlank(style)) {
            Arrays.stream(StringUtils.split(style, ';')).forEach(stylePair -> {
                String[] values = StringUtils.split(stylePair, ':');
                String name = values[0].toLowerCase();
                String value = values[1].toLowerCase();
                switch (name) {
                    case ATTR_FILL:
                        setFill(parsePaint(value));
                        break;
                    case ATTR_FILL_OPACITY:
                        break;
                    case ATTR_STROKE:
                        setStroke(parsePaint(value));
                        break;
                    case ATTR_STROKE_OPACITY:
                        break;
                    case ATTR_STROKE_WIDTH:
                        setStrokeWidth(Double.valueOf(value));
                        break;
                    case ATTR_STROKE_LINECAP:
                        setStrokeLineCap(parseStrokeLineCap(value));
                        break;
                    case ATTR_STROKE_LINEJOIN:
                        setStrokeLineJoin(parseStrokeLineJoin(value));
                        break;
                    case ATTR_STROKE_MITERLIMIT:
                        setStrokeMiterLimit(parseDouble(value));
                        break;
                    case ATTR_STROKE_DASHOFFSET:
                        setStrokeDashOffset(parseDouble(value));
                        break;
                    case ATTR_STROKE_DASHARRAY:
                        setStrokeDashArray(parseDoubleList(value));
                        break;
                }
            });
        }
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgStylable stylable = (ISvgStylable) object;
        SvgGraphicsStyleAttributes attributes = stylable.getGraphicsAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FILL)).findFirst().ifPresent(fld -> {
                attributes.setFill(parsePaint((String) dataRecord.get(fld)));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FILL_RULE)).findFirst().ifPresent(fld -> {
                attributes.setFillRule((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STROKE)).findFirst().ifPresent(fld -> {
                attributes.setStroke(parsePaint((String) dataRecord.get(fld)));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STROKE_DASHARRAY)).findFirst().ifPresent(fld -> {
                attributes.setStrokeDashArray(parseDoubleList((String) dataRecord.get(fld)));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STROKE_DASHOFFSET)).findFirst().ifPresent(fld -> {
                attributes.setStrokeDashOffset(parseDouble((String) dataRecord.get(fld)));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STROKE_LINECAP)).findFirst().ifPresent(fld -> {
                attributes.setStrokeLineCap(parseStrokeLineCap((String) dataRecord.get(fld)));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STROKE_LINEJOIN)).findFirst().ifPresent(fld -> {
                attributes.setStrokeLineJoin(parseStrokeLineJoin((String) dataRecord.get(fld)));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STROKE_MITERLIMIT)).findFirst().ifPresent(fld -> {
                attributes.setStrokeMiterLimit(parseDouble((String) dataRecord.get(fld)));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STROKE_WIDTH)).findFirst().ifPresent(fld -> {
                attributes.setStrokeWidth(parseDouble((String) dataRecord.get(fld)));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_COLOR)).findFirst().ifPresent(fld -> {
                attributes.setColor((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_COLOR_INTERPOLATION)).findFirst().ifPresent(fld -> {
                attributes.setColorInterpolation((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_COLOR_RENDERING)).findFirst().ifPresent(fld -> {
                attributes.setColorRendering((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_OPACITY)).findFirst().ifPresent(fld -> {
                attributes.setOpacity((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FILL_OPACITY)).findFirst().ifPresent(fld -> {
                attributes.setFillOpacity((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STROKE_OPACITY)).findFirst().ifPresent(fld -> {
                attributes.setStrokeOpacity((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_DISPLAY)).findFirst().ifPresent(fld -> {
                attributes.setDisplay((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_IMAGE_RENDERING)).findFirst().ifPresent(fld -> {
                attributes.setImageRendering((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_POINTER_EVENTS)).findFirst().ifPresent(fld -> {
                attributes.setPointerEvents((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_SHAPE_RENDERING)).findFirst().ifPresent(fld -> {
                attributes.setShapeRendering((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_TEXT_RENDERING)).findFirst().ifPresent(fld -> {
                attributes.setTextRendering((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_VISIBILITY)).findFirst().ifPresent(fld -> {
                attributes.setVisibility((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_COLOR_PROFILE)).findFirst().ifPresent(fld -> {
                attributes.setColorProfile((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STOP_COLOR)).findFirst().ifPresent(fld -> {
                attributes.setStopColor((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_STOP_OPACITY)).findFirst().ifPresent(fld -> {
                attributes.setStopOpacity((String) dataRecord.get(fld));
            });
        }
        return attributes;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgStylable stylable = (ISvgStylable) instance;
        SvgGraphicsStyleAttributes attributes = stylable.getGraphicsAttributes();
        String attributeName = StringUtils.defaultIfBlank(fieldName, "@");
        switch (StringUtils.remove(attributeName, '@')) {
            case ATTR_FILL:
                return attributes.getFill();
            case ATTR_FILL_RULE:
                return attributes.getFillRule();
            case ATTR_STROKE:
                return attributes.getStroke();
            case ATTR_STROKE_DASHARRAY:
                return attributes.getStrokeDashArray();
            case ATTR_STROKE_DASHOFFSET:
                return attributes.getStrokeDashOffset();
            case ATTR_STROKE_LINECAP:
                return attributes.getStrokeLineCap();
            case ATTR_STROKE_LINEJOIN:
                return attributes.getStrokeLineJoin();
            case ATTR_STROKE_MITERLIMIT:
                return attributes.getStrokeMiterLimit();
            case ATTR_STROKE_WIDTH:
                return attributes.getStrokeWidth();
            case ATTR_COLOR:
                return attributes.getColor();
            case ATTR_COLOR_INTERPOLATION:
                return attributes.getColorInterpolation();
            case ATTR_COLOR_RENDERING:
                return attributes.getColorRendering();
            case ATTR_OPACITY:
                return attributes.getOpacity();
            case ATTR_FILL_OPACITY:
                return attributes.getFillOpacity();
            case ATTR_STROKE_OPACITY:
                return attributes.getStrokeOpacity();
            case ATTR_DISPLAY:
                return attributes.getDisplay();
            case ATTR_IMAGE_RENDERING:
                return attributes.getImageRendering();
            case ATTR_POINTER_EVENTS:
                return attributes.getPointerEvents();
            case ATTR_SHAPE_RENDERING:
                return attributes.getShapeRendering();
            case ATTR_TEXT_RENDERING:
                return attributes.getTextRendering();
            case ATTR_VISIBILITY:
                return attributes.getVisibility();
            case ATTR_COLOR_PROFILE:
                return attributes.getColorProfile();
            case ATTR_STOP_COLOR:
                return attributes.getStopColor();
            case ATTR_STOP_OPACITY:
                return attributes.getStopOpacity();
            default:
                return null;
        }
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_FILL, fill);
        builder.add(ATTR_FILL_RULE, fillRule);
        builder.add(ATTR_STROKE, stroke);
        builder.add(ATTR_STROKE_DASHARRAY, strokeDashArray);
        builder.add(ATTR_STROKE_DASHOFFSET, strokeDashOffset);
        builder.add(ATTR_STROKE_LINECAP, strokeLineCap);
        builder.add(ATTR_STROKE_LINEJOIN, strokeLineJoin);
        builder.add(ATTR_STROKE_MITERLIMIT, strokeMiterLimit);
        builder.add(ATTR_STROKE_WIDTH, strokeWidth);
        builder.add(ATTR_COLOR, color);
        builder.add(ATTR_COLOR_INTERPOLATION, colorInterpolation);
        builder.add(ATTR_COLOR_RENDERING, colorRendering);
        builder.add(ATTR_OPACITY, opacity);
        builder.add(ATTR_FILL_OPACITY, fillOpacity);
        builder.add(ATTR_STROKE_OPACITY, strokeOpacity);
        builder.add(ATTR_DISPLAY, display);
        builder.add(ATTR_IMAGE_RENDERING, imageRendering);
        builder.add(ATTR_POINTER_EVENTS, pointerEvents);
        builder.add(ATTR_SHAPE_RENDERING, shapeRendering);
        builder.add(ATTR_TEXT_RENDERING, textRendering);
        builder.add(ATTR_VISIBILITY, visibility);
        builder.add(ATTR_COLOR_PROFILE, colorProfile);
        builder.add(ATTR_STOP_COLOR, stopColor);
        builder.add(ATTR_STOP_OPACITY, stopOpacity);
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
