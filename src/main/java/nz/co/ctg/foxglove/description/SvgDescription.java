package nz.co.ctg.foxglove.description;

import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgValueElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "desc")
public class SvgDescription extends AbstractSvgStylable implements ISvgDescriptiveElement, ISvgValueElement {

    @XmlValueExtension
    private String value;

    @XmlAttribute(name = "style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String style;

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String className;

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
