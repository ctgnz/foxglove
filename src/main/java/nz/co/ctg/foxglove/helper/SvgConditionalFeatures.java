package nz.co.ctg.foxglove.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgConditionalFeatures {
    @XmlAttribute(name = "requiredFeatures")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String requiredFeatures;

    @XmlAttribute(name = "requiredExtensions")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String requiredExtensions;

    @XmlAttribute(name = "systemLanguage")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String systemLanguage;

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

}
