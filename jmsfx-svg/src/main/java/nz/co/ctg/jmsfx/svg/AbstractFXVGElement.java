package nz.co.ctg.jmsfx.svg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractFXVGElement implements FXVGElement {

    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;

    @XmlAttribute(name = "xml:base")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlBase;

    @XmlAttribute(name = "xml:lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xmlLang;

    @XmlAttribute(name = "xml:space")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xmlSpace;

    public AbstractFXVGElement() {
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the xmlBase property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXmlBase() {
        return xmlBase;
    }

    /**
     * Sets the value of the xmlBase property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXmlBase(String value) {
        this.xmlBase = value;
    }

    /**
     * Gets the value of the xmlLang property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXmlLang() {
        return xmlLang;
    }

    /**
     * Sets the value of the xmlLang property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXmlLang(String value) {
        this.xmlLang = value;
    }

    /**
     * Gets the value of the xmlSpace property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXmlSpace() {
        return xmlSpace;
    }

    /**
     * Sets the value of the xmlSpace property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXmlSpace(String value) {
        this.xmlSpace = value;
    }

    @Override
    public String toString() {
        ToStringHelper builder = MoreObjects.toStringHelper(this).omitNullValues();
        buildToString(builder);
        return builder.toString();
    }

    protected void buildToString(ToStringHelper builder) {
        builder.add("id", id);
        builder.add("xmlBase", xmlBase);
        builder.add("xmlLang", xmlLang);
        builder.add("xmlSpace", xmlSpace);
    }

}