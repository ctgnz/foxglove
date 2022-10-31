package nz.co.ctg.foxglove.attributes;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

public class TransformAttributeTransformer implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;

    private AbstractTransformationMapping mapping;

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        SvgTransformAttribute attr = new SvgTransformAttribute();
        mapping.getFields().stream().filter(fld -> fld.getName().contains("transform")).findFirst().ifPresent(fld -> {
            if (dataRecord != null) {
                attr.setTransform((String) dataRecord.get(fld));
            }
        });
        return attr;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        SvgTransformAttribute value = (SvgTransformAttribute) mapping.getAttributeValueFromObject(instance);
        return value != null ? value.getTransform() : null;
    }

}
