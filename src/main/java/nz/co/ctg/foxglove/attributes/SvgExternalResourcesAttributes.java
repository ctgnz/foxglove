package nz.co.ctg.foxglove.attributes;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgExternalResources;

import static com.google.common.base.MoreObjects.toStringHelper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgExternalResourcesAttributes implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_EXTERNAL_RESOURCES_REQUIRED = "externalResourcesRequired";

    @XmlAttribute(name = ATTR_EXTERNAL_RESOURCES_REQUIRED)
    private boolean externalResourcesRequired;
    private AbstractTransformationMapping mapping;

    public SvgExternalResourcesAttributes() {
    }

    public boolean isExternalResourcesRequired() {
        return externalResourcesRequired;
    }

    public void setExternalResourcesRequired(boolean value) {
        this.externalResourcesRequired = value;
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper(ISvgExternalResources.class.getSimpleName()).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_EXTERNAL_RESOURCES_REQUIRED, isExternalResourcesRequired());
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgExternalResources extResources = (ISvgExternalResources) object;
        SvgExternalResourcesAttributes attributes = extResources.getExternalResourcesAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_EXTERNAL_RESOURCES_REQUIRED)).findFirst().ifPresent(fld -> {
                attributes.setExternalResourcesRequired(Boolean.valueOf((String) dataRecord.get(fld)));
            });
        }
        return attributes;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgExternalResources extResources = (ISvgExternalResources) instance;
        SvgExternalResourcesAttributes attributes = extResources.getExternalResourcesAttributes();
        return attributes.isExternalResourcesRequired();
    }

}
