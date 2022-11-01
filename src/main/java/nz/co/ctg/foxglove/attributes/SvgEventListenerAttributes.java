package nz.co.ctg.foxglove.attributes;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgEventListener;

import static com.google.common.base.MoreObjects.toStringHelper;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class SvgEventListenerAttributes implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_ONLOAD = "onload";
    private static final String ATTR_ONMOUSEOUT = "onmouseout";
    private static final String ATTR_ONMOUSEMOVE = "onmousemove";
    private static final String ATTR_ONMOUSEOVER = "onmouseover";
    private static final String ATTR_ONMOUSEUP = "onmouseup";
    private static final String ATTR_ONMOUSEDOWN = "onmousedown";
    private static final String ATTR_ONCLICK = "onclick";
    private static final String ATTR_ONACTIVATE = "onactivate";
    private static final String ATTR_ONFOCUSOUT = "onfocusout";
    private static final String ATTR_ONFOCUSIN = "onfocusin";

    public SvgEventListenerAttributes() {
    }

    @XmlAttribute(name = ATTR_ONFOCUSIN)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onFocusIn;

    @XmlAttribute(name = ATTR_ONFOCUSOUT)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onFocusOut;

    @XmlAttribute(name = ATTR_ONACTIVATE)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onActivate;

    @XmlAttribute(name = ATTR_ONCLICK)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onClick;

    @XmlAttribute(name = ATTR_ONMOUSEDOWN)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseDown;

    @XmlAttribute(name = ATTR_ONMOUSEUP)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseUp;

    @XmlAttribute(name = ATTR_ONMOUSEOVER)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseOver;

    @XmlAttribute(name = ATTR_ONMOUSEMOVE)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseMove;

    @XmlAttribute(name = ATTR_ONMOUSEOUT)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseOut;

    @XmlAttribute(name = ATTR_ONLOAD)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onLoad;
    private AbstractTransformationMapping mapping;

    public String getOnFocusIn() {
        return onFocusIn;
    }

    public void setOnFocusIn(String value) {
        this.onFocusIn = value;
    }

    public String getOnFocusOut() {
        return onFocusOut;
    }

    public void setOnFocusOut(String value) {
        this.onFocusOut = value;
    }

    public String getOnActivate() {
        return onActivate;
    }

    public void setOnActivate(String value) {
        this.onActivate = value;
    }

    public String getOnClick() {
        return onClick;
    }

    public void setOnClick(String value) {
        this.onClick = value;
    }

    public String getOnMouseDown() {
        return onMouseDown;
    }

    public void setOnMouseDown(String value) {
        this.onMouseDown = value;
    }

    public String getOnMouseUp() {
        return onMouseUp;
    }

    public void setOnMouseUp(String value) {
        this.onMouseUp = value;
    }

    public String getOnMouseOver() {
        return onMouseOver;
    }

    public void setOnMouseOver(String value) {
        this.onMouseOver = value;
    }

    public String getOnMouseMove() {
        return onMouseMove;
    }

    public void setOnMouseMove(String value) {
        this.onMouseMove = value;
    }

    public String getOnMouseOut() {
        return onMouseOut;
    }

    public void setOnMouseOut(String value) {
        this.onMouseOut = value;
    }

    public String getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(String value) {
        this.onLoad = value;
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper(ISvgEventListener.class.getSimpleName()).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_ONFOCUSIN, getOnFocusIn());
        builder.add(ATTR_ONFOCUSOUT, getOnFocusOut());
        builder.add(ATTR_ONACTIVATE, getOnActivate());
        builder.add(ATTR_ONCLICK, getOnClick());
        builder.add(ATTR_ONMOUSEDOWN, getOnMouseDown());
        builder.add(ATTR_ONMOUSEUP, getOnMouseUp());
        builder.add(ATTR_ONMOUSEOVER, getOnMouseOver());
        builder.add(ATTR_ONMOUSEMOVE, getOnMouseMove());
        builder.add(ATTR_ONMOUSEOUT, getOnMouseOut());
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgEventListener listener = (ISvgEventListener) instance;
        SvgEventListenerAttributes attributes = listener.getEventListenerAttributes();
        String attributeName = StringUtils.defaultIfBlank(fieldName, "@");
        switch (StringUtils.remove(attributeName, '@')) {
            case ATTR_ONFOCUSIN:
                return attributes.getOnFocusIn();
            case ATTR_ONFOCUSOUT:
                return attributes.getOnFocusOut();
            case ATTR_ONACTIVATE:
                return attributes.getOnActivate();
            case ATTR_ONCLICK:
                return attributes.getOnClick();
            case ATTR_ONMOUSEDOWN:
                return attributes.getOnMouseDown();
            case ATTR_ONMOUSEUP:
                return attributes.getOnMouseUp();
            case ATTR_ONMOUSEOVER:
                return attributes.getOnMouseOver();
            case ATTR_ONMOUSEMOVE:
                return attributes.getOnMouseMove();
            case ATTR_ONMOUSEOUT:
                return attributes.getOnMouseOut();
            case ATTR_ONLOAD:
                return attributes.getOnLoad();
            default: return null;
        }
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgEventListener listener = (ISvgEventListener) object;
        SvgEventListenerAttributes attributes = listener.getEventListenerAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONFOCUSIN)).findFirst().ifPresent(fld -> {
                attributes.setOnFocusIn((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONFOCUSOUT)).findFirst().ifPresent(fld -> {
                attributes.setOnFocusOut((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONACTIVATE)).findFirst().ifPresent(fld -> {
                attributes.setOnActivate((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONCLICK)).findFirst().ifPresent(fld -> {
                attributes.setOnClick((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONMOUSEDOWN)).findFirst().ifPresent(fld -> {
                attributes.setOnMouseDown((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONMOUSEUP)).findFirst().ifPresent(fld -> {
                attributes.setOnMouseUp((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONMOUSEOVER)).findFirst().ifPresent(fld -> {
                attributes.setOnMouseOver((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONMOUSEMOVE)).findFirst().ifPresent(fld -> {
                attributes.setOnMouseMove((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONMOUSEOUT)).findFirst().ifPresent(fld -> {
                attributes.setOnMouseOut((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ONLOAD)).findFirst().ifPresent(fld -> {
                attributes.setOnLoad((String) dataRecord.get(fld));
            });
        }
        return attributes;
    }
}
