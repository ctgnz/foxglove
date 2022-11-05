package nz.co.ctg.foxglove.description;

import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgValueElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "metadata")
public class SvgMetadata extends AbstractSvgElement implements ISvgDescriptiveElement, ISvgValueElement {

    @XmlValue
    @XmlValueExtension
    protected String value;

    public SvgMetadata() {
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        builder.add("value", value);
    }
}
