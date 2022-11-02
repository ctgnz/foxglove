package nz.co.ctg.foxglove.attributes;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgStylable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgPresentationStyleAttributes implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_FLOOD_COLOR = "flood-color";
    private static final String ATTR_FLOOD_OPACITY = "flood-opacity";
    private static final String ATTR_LIGHTING_COLOR = "lighting-color";
    private static final String ATTR_ENABLE_BACKGROUND = "enable-background";
    private static final String ATTR_CLIP = "clip";
    private static final String ATTR_OVERFLOW = "overflow";
    private static final String ATTR_MARKER_START = "marker-start";
    private static final String ATTR_MARKER_MID = "marker-mid";
    private static final String ATTR_MARKER_END = "marker-end";
    private static final String ATTR_CLIP_PATH = "clip-path";
    private static final String ATTR_CLIP_RULE = "clip-rule";
    private static final String ATTR_MASK = "mask";
    private static final String ATTR_FILTER = "filter";
    private static final String ATTR_COLOR_INTERPOLATION_FILTERS = "color-interpolation-filters";
    private static final String ATTR_CURSOR = "cursor";

    private AbstractTransformationMapping mapping;

    @XmlAttribute(name = ATTR_FLOOD_COLOR)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String floodColor;

    @XmlAttribute(name = ATTR_FLOOD_OPACITY)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String floodOpacity;

    @XmlAttribute(name = ATTR_LIGHTING_COLOR)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String lightingColor;

    @XmlAttribute(name = ATTR_ENABLE_BACKGROUND)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String enableBackground;

    @XmlAttribute(name = ATTR_CLIP)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String clip;

    @XmlAttribute(name = ATTR_OVERFLOW)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String overflow;

    @XmlAttribute(name = ATTR_MARKER_START)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String markerStart;

    @XmlAttribute(name = ATTR_MARKER_MID)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String markerMid;

    @XmlAttribute(name = ATTR_MARKER_END)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String markerEnd;

    @XmlAttribute(name = ATTR_CLIP_PATH)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String clipPath;

    @XmlAttribute(name = ATTR_CLIP_RULE)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String clipRule;

    @XmlAttribute(name = ATTR_MASK)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String mask;

    @XmlAttribute(name = ATTR_FILTER)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String filter;

    @XmlAttribute(name = ATTR_COLOR_INTERPOLATION_FILTERS)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String colorInterpolationFilters;

    @XmlAttribute(name = ATTR_CURSOR)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String cursor;

    public SvgPresentationStyleAttributes() {
    }

    public String getFloodColor() {
        return floodColor;
    }

    public void setFloodColor(String floodColor) {
        this.floodColor = floodColor;
    }

    public String getFloodOpacity() {
        return floodOpacity;
    }

    public void setFloodOpacity(String floodOpacity) {
        this.floodOpacity = floodOpacity;
    }

    public String getLightingColor() {
        return lightingColor;
    }

    public void setLightingColor(String lightingColor) {
        this.lightingColor = lightingColor;
    }

    public String getEnableBackground() {
        return enableBackground;
    }

    public void setEnableBackground(String enableBackground) {
        this.enableBackground = enableBackground;
    }

    public String getClip() {
        return clip;
    }

    public void setClip(String clip) {
        this.clip = clip;
    }

    public String getOverflow() {
        return overflow;
    }

    public void setOverflow(String overflow) {
        this.overflow = overflow;
    }

    public String getMarkerStart() {
        return markerStart;
    }

    public void setMarkerStart(String markerStart) {
        this.markerStart = markerStart;
    }

    public String getMarkerMid() {
        return markerMid;
    }

    public void setMarkerMid(String markerMid) {
        this.markerMid = markerMid;
    }

    public String getMarkerEnd() {
        return markerEnd;
    }

    public void setMarkerEnd(String markerEnd) {
        this.markerEnd = markerEnd;
    }

    public String getClipPath() {
        return clipPath;
    }

    public void setClipPath(String clipPath) {
        this.clipPath = clipPath;
    }

    public String getClipRule() {
        return clipRule;
    }

    public void setClipRule(String clipRule) {
        this.clipRule = clipRule;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getColorInterpolationFilters() {
        return colorInterpolationFilters;
    }

    public void setColorInterpolationFilters(String colorInterpolationFilters) {
        this.colorInterpolationFilters = colorInterpolationFilters;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgStylable stylable = (ISvgStylable) object;
        SvgPresentationStyleAttributes attributes = stylable.getPresentationAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FLOOD_COLOR)).findFirst().ifPresent(fld -> {
                attributes.setFloodColor((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FLOOD_OPACITY)).findFirst().ifPresent(fld -> {
                attributes.setFloodOpacity((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_LIGHTING_COLOR)).findFirst().ifPresent(fld -> {
                attributes.setLightingColor((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ENABLE_BACKGROUND)).findFirst().ifPresent(fld -> {
                attributes.setEnableBackground((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_CLIP)).findFirst().ifPresent(fld -> {
                attributes.setClip((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_OVERFLOW)).findFirst().ifPresent(fld -> {
                attributes.setOverflow((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_MARKER_START)).findFirst().ifPresent(fld -> {
                attributes.setMarkerStart((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_MARKER_MID)).findFirst().ifPresent(fld -> {
                attributes.setMarkerMid((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_MARKER_END)).findFirst().ifPresent(fld -> {
                attributes.setMarkerEnd((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_CLIP_PATH)).findFirst().ifPresent(fld -> {
                attributes.setClipPath((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_CLIP_RULE)).findFirst().ifPresent(fld -> {
                attributes.setClipRule((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_MASK)).findFirst().ifPresent(fld -> {
                attributes.setMask((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FILTER)).findFirst().ifPresent(fld -> {
                attributes.setFilter((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_COLOR_INTERPOLATION_FILTERS)).findFirst().ifPresent(fld -> {
                attributes.setColorInterpolationFilters((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_CURSOR)).findFirst().ifPresent(fld -> {
                attributes.setCursor((String) dataRecord.get(fld));
            });
        }
        return attributes;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgStylable stylable = (ISvgStylable) instance;
        SvgPresentationStyleAttributes attributes = stylable.getPresentationAttributes();
        String attributeName = StringUtils.defaultIfBlank(fieldName, "@");
        switch (StringUtils.remove(attributeName, '@')) {
            case ATTR_FLOOD_COLOR:
                return attributes.getFloodColor();
            case ATTR_FLOOD_OPACITY:
                return attributes.getFloodOpacity();
            case ATTR_LIGHTING_COLOR:
                return attributes.getLightingColor();
            case ATTR_ENABLE_BACKGROUND:
                return attributes.getEnableBackground();
            case ATTR_CLIP:
                return attributes.getClip();
            case ATTR_OVERFLOW:
                return attributes.getOverflow();
            case ATTR_MARKER_START:
                return attributes.getMarkerStart();
            case ATTR_MARKER_MID:
                return attributes.getMarkerMid();
            case ATTR_MARKER_END:
                return attributes.getMarkerEnd();
            case ATTR_CLIP_PATH:
                return attributes.getClipPath();
            case ATTR_CLIP_RULE:
                return attributes.getClipRule();
            case ATTR_MASK:
                return attributes.getMask();
            case ATTR_FILTER:
                return attributes.getFilter();
            case ATTR_COLOR_INTERPOLATION_FILTERS:
                return attributes.getColorInterpolationFilters();
            case ATTR_CURSOR:
                return attributes.getCursor();
            default:
                return null;
        }
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_ENABLE_BACKGROUND, enableBackground);
        builder.add(ATTR_CLIP, clip);
        builder.add(ATTR_OVERFLOW, overflow);
        builder.add(ATTR_MARKER_START, markerStart);
        builder.add(ATTR_MARKER_MID, markerMid);
        builder.add(ATTR_MARKER_END, markerEnd);
        builder.add(ATTR_CLIP_PATH, clipPath);
        builder.add(ATTR_CLIP_RULE, clipRule);
        builder.add(ATTR_MASK, mask);
        builder.add(ATTR_FILTER, filter);
        builder.add(ATTR_COLOR_INTERPOLATION_FILTERS, colorInterpolationFilters);
        builder.add(ATTR_CURSOR, cursor);
        builder.add(ATTR_FLOOD_COLOR, floodColor);
        builder.add(ATTR_FLOOD_OPACITY, floodOpacity);
        builder.add(ATTR_LIGHTING_COLOR, lightingColor);
    }

}
