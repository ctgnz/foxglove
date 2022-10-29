package nz.co.ctg.foxglove.attributes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgExternalResourcesAttributes {

    @XmlAttribute(name = "externalResourcesRequired")
    private boolean externalResourcesRequired;

    public boolean isExternalResourcesRequired() {
        return externalResourcesRequired;
    }

    public void setExternalResourcesRequired(boolean value) {
        this.externalResourcesRequired = value;
    }

}
