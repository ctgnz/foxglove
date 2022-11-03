package nz.co.ctg.foxglove.text;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "vkern")
public class SvgVerticalKerning extends AbstractSvgElement {

    @XmlAttribute(name = "u1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String unicodeChars1;

    @XmlAttribute(name = "g1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String glyphs1;

    @XmlAttribute(name = "u2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String unicodeChars2;

    @XmlAttribute(name = "g2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String glyphs2;

    @XmlAttribute(name = "k", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String kern;

    public String getUnicodeChars1() {
        return unicodeChars1;
    }

    public void setUnicodeChars1(String value) {
        this.unicodeChars1 = value;
    }

    public String getGlyphs1() {
        return glyphs1;
    }

    public void setGlyphs1(String value) {
        this.glyphs1 = value;
    }

    public String getUnicodeChars2() {
        return unicodeChars2;
    }

    public void setUnicodeChars2(String value) {
        this.unicodeChars2 = value;
    }

    public String getGlyphs2() {
        return glyphs2;
    }

    public void setGlyphs2(String value) {
        this.glyphs2 = value;
    }

    public String getKern() {
        return kern;
    }

    public void setKern(String value) {
        this.kern = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("unicodeChars1", unicodeChars1);
        builder.add("glyphs1", glyphs1);
        builder.add("unicodeChars2", unicodeChars2);
        builder.add("glyphs2", glyphs2);
        builder.add("kern", kern);
        super.toStringDetail(builder);
    }

}
