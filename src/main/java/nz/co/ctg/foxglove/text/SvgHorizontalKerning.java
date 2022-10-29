package nz.co.ctg.foxglove.text;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nz.co.ctg.foxglove.AbstractSvgElement;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "hkern")
public class SvgHorizontalKerning extends AbstractSvgElement {

    @XmlAttribute(name = "u1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String unicodeChars1;

    @XmlAttribute(name = "g1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String glyphs1;

    @XmlAttribute(name = "u2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String unicodeChars2;

    @XmlAttribute(name = "g2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String glyphs2;

    @XmlAttribute(name = "k", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String kern;

    /**
     * Gets the value of the u1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnicodeChars1() {
        return unicodeChars1;
    }

    /**
     * Sets the value of the u1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnicodeChars1(String value) {
        this.unicodeChars1 = value;
    }

    /**
     * Gets the value of the g1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGlyphs1() {
        return glyphs1;
    }

    /**
     * Sets the value of the g1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGlyphs1(String value) {
        this.glyphs1 = value;
    }

    /**
     * Gets the value of the u2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnicodeChars2() {
        return unicodeChars2;
    }

    /**
     * Sets the value of the u2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnicodeChars2(String value) {
        this.unicodeChars2 = value;
    }

    /**
     * Gets the value of the g2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGlyphs2() {
        return glyphs2;
    }

    /**
     * Sets the value of the g2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGlyphs2(String value) {
        this.glyphs2 = value;
    }

    /**
     * Gets the value of the k property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKern() {
        return kern;
    }

    /**
     * Sets the value of the k property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKern(String value) {
        this.kern = value;
    }

}
