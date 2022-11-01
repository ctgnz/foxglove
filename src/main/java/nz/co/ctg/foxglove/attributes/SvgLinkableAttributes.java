package nz.co.ctg.foxglove.attributes;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgLinkable;

import static com.google.common.base.MoreObjects.toStringHelper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgLinkableAttributes implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_XLINK_NAMESPACE = "xmlns:xlink";
    private static final String ATTR_TYPE = "xlink:type";
    private static final String ATTR_HREF = "xlink:href";
    private static final String ATTR_ROLE = "xlink:role";
    private static final String ATTR_ARCROLE = "xlink:arcrole";
    private static final String ATTR_TITLE = "xlink:title";
    private static final String ATTR_SHOW = "xlink:show";
    private static final String ATTR_ACTUATE = "xlink:actuate";

    public SvgLinkableAttributes() {
    }

    @XmlAttribute(name = ATTR_XLINK_NAMESPACE)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkNamespace;

    @XmlAttribute(name = ATTR_TYPE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String type;

    @XmlAttribute(name = ATTR_HREF, required = true, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String href;

    @XmlAttribute(name = ATTR_ROLE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String role;

    @XmlAttribute(name = ATTR_ARCROLE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String arcRole;

    @XmlAttribute(name = ATTR_TITLE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String title;

    @XmlAttribute(name = ATTR_SHOW, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String show;

    @XmlAttribute(name = ATTR_ACTUATE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String actuate;

    private AbstractTransformationMapping mapping;

    public String getXlinkNamespace() {
        if (xlinkNamespace == null) {
            return "http://www.w3.org/1999/xlink";
        } else {
            return xlinkNamespace;
        }
    }

    public void setXlinkNamespace(String value) {
        this.xlinkNamespace = value;
    }

    public String getType() {
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String value) {
        this.role = value;
    }

    public String getArcRole() {
        return arcRole;
    }

    public void setArcRole(String value) {
        this.arcRole = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getShow() {
        if (show == null) {
            return "other";
        } else {
            return show;
        }
    }

    public void setShow(String value) {
        this.show = value;
    }

    public String getActuate() {
        if (actuate == null) {
            return "onLoad";
        } else {
            return actuate;
        }
    }

    public void setActuate(String value) {
        this.actuate = value;
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper(ISvgLinkable.class.getSimpleName()).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_XLINK_NAMESPACE, xlinkNamespace);
        builder.add(ATTR_TYPE, type);
        builder.add(ATTR_HREF, href);
        builder.add(ATTR_ROLE, role);
        builder.add(ATTR_ARCROLE, arcRole);
        builder.add(ATTR_TITLE, title);
        builder.add(ATTR_SHOW, show);
        builder.add(ATTR_ACTUATE, actuate);
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgLinkable features = (ISvgLinkable) object;
        SvgLinkableAttributes attributes = features.getLinkableAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_XLINK_NAMESPACE)).findFirst().ifPresent(fld -> {
                attributes.setXlinkNamespace((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_TYPE)).findFirst().ifPresent(fld -> {
                attributes.setType((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_HREF)).findFirst().ifPresent(fld -> {
                attributes.setHref((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ROLE)).findFirst().ifPresent(fld -> {
                attributes.setRole((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ARCROLE)).findFirst().ifPresent(fld -> {
                attributes.setArcRole((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_TITLE)).findFirst().ifPresent(fld -> {
                attributes.setTitle((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_SHOW)).findFirst().ifPresent(fld -> {
                attributes.setShow((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_ACTUATE)).findFirst().ifPresent(fld -> {
                attributes.setActuate((String) dataRecord.get(fld));
            });
        }
        return attributes;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgLinkable features = (ISvgLinkable) instance;
        SvgLinkableAttributes attributes = features.getLinkableAttributes();
        String attributeName = StringUtils.defaultIfBlank(fieldName, "@");
        switch (StringUtils.remove(attributeName, '@')) {
            case ATTR_XLINK_NAMESPACE:
                return attributes.getXlinkNamespace();
            case ATTR_TYPE:
                return attributes.getType();
            case ATTR_HREF:
                return attributes.getHref();
            case ATTR_ROLE:
                return attributes.getRole();
            case ATTR_ARCROLE:
                return attributes.getArcRole();
            case ATTR_TITLE:
                return attributes.getTitle();
            case ATTR_SHOW:
                return attributes.getShow();
            case ATTR_ACTUATE:
                return attributes.getActuate();
            default:
                return null;
        }
    }

}
