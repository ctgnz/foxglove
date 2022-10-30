package nz.co.ctg.foxglove.attributes;

import com.google.common.base.MoreObjects.ToStringHelper;

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

    public void toStringDetail(ToStringHelper builder) {
        builder.add("externalResourcesRequired", isExternalResourcesRequired());
    }

}
