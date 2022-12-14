package nz.co.ctg.foxglove.text;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "font-face-format")
public class SvgFontFaceFormat extends AbstractSvgElement implements ISvgElement {

    @XmlAttribute(name = "string")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String string;

    public String getString() {
        return string;
    }

    public void setString(String value) {
        this.string = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("value", string);
        super.toStringDetail(builder);
    }

}
