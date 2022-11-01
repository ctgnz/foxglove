package nz.co.ctg.foxglove.filter;

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
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgFilterAttributes implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_X = "x";
    private static final String ATTR_Y = "y";
    private static final String ATTR_WIDTH = "width";
    private static final String ATTR_HEIGHT = "height";
    private static final String ATTR_RESULT = "result";

    public SvgFilterAttributes() {
    }

    @XmlAttribute(name = ATTR_X)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String x;

    @XmlAttribute(name = ATTR_Y)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String y;

    @XmlAttribute(name = ATTR_WIDTH)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String width;

    @XmlAttribute(name = ATTR_HEIGHT)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String height;

    @XmlAttribute(name = ATTR_RESULT)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String result;
    private AbstractTransformationMapping mapping;

    public String getX() {
        return x;
    }

    public void setX(String value) {
        this.x = value;
    }

    public String getY() {
        return y;
    }

    public void setY(String value) {
        this.y = value;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String value) {
        this.width = value;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String value) {
        this.height = value;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String value) {
        this.result = value;
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_X, x);
        builder.add(ATTR_Y, y);
        builder.add(ATTR_WIDTH, width);
        builder.add(ATTR_HEIGHT, height);
        builder.add(ATTR_RESULT, result);
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgFilterPrimitive features = (ISvgFilterPrimitive) object;
        SvgFilterAttributes attributes = features.getFilterAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_X)).findFirst().ifPresent(fld -> {
                attributes.setX((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_Y)).findFirst().ifPresent(fld -> {
                attributes.setY((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_WIDTH)).findFirst().ifPresent(fld -> {
                attributes.setWidth((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_HEIGHT)).findFirst().ifPresent(fld -> {
                attributes.setHeight((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_RESULT)).findFirst().ifPresent(fld -> {
                attributes.setResult((String) dataRecord.get(fld));
            });
        }
        return attributes;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgFilterPrimitive features = (ISvgFilterPrimitive) instance;
        SvgFilterAttributes attributes = features.getFilterAttributes();
        String attributeName = StringUtils.defaultIfBlank(fieldName, "@");
        switch (StringUtils.remove(attributeName, '@')) {
            case ATTR_X:
                return attributes.getX();
            case ATTR_Y:
                return attributes.getY();
            case ATTR_WIDTH:
                return attributes.getWidth();
            case ATTR_HEIGHT:
                return attributes.getHeight();
            case ATTR_RESULT:
                return attributes.getResult();
            default:
                return null;
        }
    }

}
