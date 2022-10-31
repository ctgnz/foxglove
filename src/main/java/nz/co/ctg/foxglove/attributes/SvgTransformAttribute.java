package nz.co.ctg.foxglove.attributes;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.parser.SvgTransformListHandler;

import static com.google.common.base.MoreObjects.toStringHelper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.transform.Transform;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class SvgTransformAttribute implements AttributeTransformer, FieldTransformer{
    private static final long serialVersionUID = 1L;
    private static final String ATTR_TRANSFORM = "transform";

    private String transform;
    private AbstractTransformationMapping mapping;
    private static SvgTransformListHandler adapter = new SvgTransformListHandler();

    public SvgTransformAttribute() {
    }

    @XmlAttribute(name = ATTR_TRANSFORM)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public String getTransform() {
        return transform;
    }

    public void setTransform(String value) {
        this.transform = value;
    }

    public List<Transform> getTransformList() {
        if (StringUtils.isBlank(getTransform())) {
            return Collections.emptyList();
        }
        return adapter.parse(getTransform());
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper(ISvgTransformable.class.getSimpleName()).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_TRANSFORM, transform);
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgTransformable transformable = (ISvgTransformable) object;
        SvgTransformAttribute attr = transformable.getTransformAttribute();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_TRANSFORM)).findFirst().ifPresent(fld -> {
                    attr.setTransform((String) dataRecord.get(fld));
            });
        }
        return attr;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgTransformable transformable = (ISvgTransformable) instance;
        SvgTransformAttribute value = transformable.getTransformAttribute();
        return value.getTransform();
    }

}
