package nz.co.ctg.foxglove.attributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgConditionalFeatures;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.stream.Collectors.toList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgConditionalFeaturesAttributes implements AttributeTransformer, FieldTransformer {
    private static final long serialVersionUID = 1L;
    private static final String ATTR_REQUIRED_FEATURES = "requiredFeatures";
    private static final String ATTR_REQUIRED_EXTENSIONS = "requiredExtensions";
    private static final String ATTR_SYSTEM_LANGUAGE = "systemLanguage";

    @XmlAttribute(name = ATTR_REQUIRED_FEATURES)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String requiredFeatures;

    @XmlAttribute(name = ATTR_REQUIRED_EXTENSIONS)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String requiredExtensions;

    @XmlAttribute(name = ATTR_SYSTEM_LANGUAGE)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String systemLanguage;
    private AbstractTransformationMapping mapping;

    public SvgConditionalFeaturesAttributes() {
    }

    public String getRequiredFeatures() {
        return requiredFeatures;
    }

    public void setRequiredFeatures(String value) {
        this.requiredFeatures = value;
    }

    public String getRequiredExtensions() {
        return requiredExtensions;
    }

    public void setRequiredExtensions(String value) {
        this.requiredExtensions = value;
    }

    public String getSystemLanguage() {
        return systemLanguage;
    }

    public void setSystemLanguage(String value) {
        this.systemLanguage = value;
    }

    public List<String> getRequiredFeaturesList() {
        return requiredFeatures != null ? Arrays.stream(requiredFeatures.split(",")).collect(toList()) : Collections.emptyList();
    }

    public List<String> getRequiredExtensionsList() {
        return requiredExtensions != null ? Arrays.stream(requiredExtensions.split(",")).collect(toList()) : Collections.emptyList();
    }

    public List<String> getSystemLanguageList() {
        return systemLanguage != null ? Arrays.stream(systemLanguage.split(",")).collect(toList()) : Collections.emptyList();
    }

    public boolean hasExtension(String extension) {
        return getRequiredExtensionsList().contains(extension);
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper(ISvgConditionalFeatures.class.getSimpleName()).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_REQUIRED_FEATURES, getRequiredFeatures());
        builder.add(ATTR_REQUIRED_EXTENSIONS, getRequiredExtensions());
        builder.add(ATTR_SYSTEM_LANGUAGE, getSystemLanguage());
    }

    @Override
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object buildAttributeValue(DataRecord dataRecord, Object object, Session session) {
        ISvgConditionalFeatures features = (ISvgConditionalFeatures) object;
        SvgConditionalFeaturesAttributes attributes = features.getConditionalFeaturesAttributes();
        if (dataRecord != null) {
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_REQUIRED_EXTENSIONS)).findFirst().ifPresent(fld -> {
                attributes.setRequiredExtensions((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_REQUIRED_FEATURES)).findFirst().ifPresent(fld -> {
                attributes.setRequiredFeatures((String) dataRecord.get(fld));
            });
            mapping.getFields().stream().filter(fld -> fld.getName().endsWith(ATTR_SYSTEM_LANGUAGE)).findFirst().ifPresent(fld -> {
                attributes.setSystemLanguage((String) dataRecord.get(fld));
            });
        }
        return attributes;
    }

    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        ISvgConditionalFeatures features = (ISvgConditionalFeatures) instance;
        SvgConditionalFeaturesAttributes attributes = features.getConditionalFeaturesAttributes();
        String attributeName = StringUtils.defaultIfBlank(fieldName, "@");
        switch (StringUtils.remove(attributeName, '@')) {
            case ATTR_REQUIRED_FEATURES:
                return attributes.getRequiredFeatures();
            case ATTR_REQUIRED_EXTENSIONS:
                return attributes.getRequiredExtensions();
            case ATTR_SYSTEM_LANGUAGE:
                return attributes.getSystemLanguage();
            default:
                return null;
        }
    }

}
