package nz.co.ctg.foxglove.animate;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgAnimationAttributes implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_ONBEGIN = "onbegin";
    private static final String ATTR_ONEND = "onend";
    private static final String ATTR_ONREPEAT = "onrepeat";
    private static final String ATTR_ONLOAD = "onload";
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DUR = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_MIN = "min";
    private static final String ATTR_MAX = "max";
    private static final String ATTR_RESTART = "restart";
    private static final String ATTR_REPEAT_COUNT = "repeatCount";
    private static final String ATTR_REPEAT_DUR = "repeatDur";
    private static final String ATTR_FILL = "fill";
    private static final String ATTR_TO = "to";


    @XmlAttribute(name = ATTR_ONBEGIN)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String onBegin;

    @XmlAttribute(name = ATTR_ONEND)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String onEnd;

    @XmlAttribute(name = ATTR_ONREPEAT)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String onRepeat;

    @XmlAttribute(name = ATTR_ONLOAD)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String onLoad;

    @XmlAttribute(name = ATTR_BEGIN)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String begin;

    @XmlAttribute(name = ATTR_DUR)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String duration;

    @XmlAttribute(name = ATTR_END)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String end;

    @XmlAttribute(name = ATTR_MIN)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String min;

    @XmlAttribute(name = ATTR_MAX)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String max;

    @XmlAttribute(name = ATTR_RESTART)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String restart;

    @XmlAttribute(name = ATTR_REPEAT_COUNT)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String repeatCount;

    @XmlAttribute(name = ATTR_REPEAT_DUR)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String repeatDuration;

    @XmlAttribute(name = ATTR_FILL)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String fill;

    @XmlAttribute(name = ATTR_TO)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String to;
    private AbstractTransformationMapping mapping;

    public String getOnBegin() {
        return onBegin;
    }

    public void setOnBegin(String value) {
        this.onBegin = value;
    }

    public String getOnEnd() {
        return onEnd;
    }

    public void setOnEnd(String value) {
        this.onEnd = value;
    }

    public String getOnRepeat() {
        return onRepeat;
    }

    public void setOnRepeat(String value) {
        this.onRepeat = value;
    }

    public String getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(String value) {
        this.onLoad = value;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String value) {
        this.begin = value;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String value) {
        this.duration = value;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String value) {
        this.end = value;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String value) {
        this.min = value;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String value) {
        this.max = value;
    }

    public String getRestart() {
        if (restart == null) {
            return "always";
        } else {
            return restart;
        }
    }

    public void setRestart(String value) {
        this.restart = value;
    }

    public String getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(String value) {
        this.repeatCount = value;
    }

    public String getRepeatDuration() {
        return repeatDuration;
    }

    public void setRepeatDuration(String value) {
        this.repeatDuration = value;
    }

    public String getFill() {
        if (fill == null) {
            return "remove";
        } else {
            return fill;
        }
    }

    public void setFill(String value) {
        this.fill = value;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String value) {
        this.to = value;
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_ONBEGIN, onBegin);
        builder.add(ATTR_ONEND, onEnd);
        builder.add(ATTR_ONREPEAT, onRepeat);
        builder.add(ATTR_ONLOAD, onLoad);
        builder.add(ATTR_BEGIN, begin);
        builder.add(ATTR_DUR, duration);
        builder.add(ATTR_END, end);
        builder.add(ATTR_MIN, min);
        builder.add(ATTR_MAX, max);
        builder.add(ATTR_RESTART, restart);
        builder.add(ATTR_REPEAT_COUNT, repeatCount);
        builder.add(ATTR_REPEAT_DUR, repeatDuration);
        builder.add(ATTR_FILL, fill);
        builder.add(ATTR_TO, to);
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgAnimationElement animation = (ISvgAnimationElement) object;
        SvgAnimationAttributes attributes = animation.getAnimationAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONBEGIN)).findFirst().ifPresent(fld -> {
                attributes.setOnBegin((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONEND)).findFirst().ifPresent(fld -> {
                attributes.setOnEnd((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONREPEAT)).findFirst().ifPresent(fld -> {
                attributes.setOnRepeat((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONLOAD)).findFirst().ifPresent(fld -> {
                attributes.setOnLoad((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_BEGIN)).findFirst().ifPresent(fld -> {
                attributes.setBegin((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_DUR)).findFirst().ifPresent(fld -> {
                attributes.setDuration((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_END)).findFirst().ifPresent(fld -> {
                attributes.setEnd((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_MIN)).findFirst().ifPresent(fld -> {
                attributes.setMin((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_MAX)).findFirst().ifPresent(fld -> {
                attributes.setMax((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_RESTART)).findFirst().ifPresent(fld -> {
                attributes.setRestart((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_REPEAT_COUNT)).findFirst().ifPresent(fld -> {
                attributes.setRepeatCount((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_REPEAT_DUR)).findFirst().ifPresent(fld -> {
                attributes.setRepeatDuration((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_FILL)).findFirst().ifPresent(fld -> {
                attributes.setFill((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_TO)).findFirst().ifPresent(fld -> {
                attributes.setTo((String) dataRecord.get(fld));
            });
        }
        return attributes;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgAnimationElement animation = (ISvgAnimationElement) instance;
        SvgAnimationAttributes attributes = animation.getAnimationAttributes();
        String attributeName = StringUtils.defaultIfBlank(fieldName, "@");
        switch (StringUtils.remove(attributeName, '@')) {
            case ATTR_ONBEGIN:
                return attributes.getOnBegin();
            case ATTR_ONEND:
                return attributes.getOnEnd();
            case ATTR_ONREPEAT:
                return attributes.getOnRepeat();
            case ATTR_ONLOAD:
                return attributes.getOnLoad();
            case ATTR_BEGIN:
                return attributes.getBegin();
            case ATTR_DUR:
                return attributes.getDuration();
            case ATTR_END:
                return attributes.getEnd();
            case ATTR_MIN:
                return attributes.getMin();
            case ATTR_MAX:
                return attributes.getMax();
            case ATTR_RESTART:
                return attributes.getRestart();
            case ATTR_REPEAT_COUNT:
                return attributes.getRepeatCount();
            case ATTR_REPEAT_DUR:
                return attributes.getRepeatDuration();
            case ATTR_FILL:
                return attributes.getFill();
            case ATTR_TO:
                return attributes.getTo();
            default:
                return null;
        }
    }

}
