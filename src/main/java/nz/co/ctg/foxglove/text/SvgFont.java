package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "externalResources", "content"
})
@XmlRootElement(name = "font")
public class SvgFont extends AbstractSvgStylable implements ISvgExternalResources {

    @XmlPath(".")
    protected final SvgExternalResourcesAttributes externalResources = new SvgExternalResourcesAttributes();

    @XmlAttribute(name = "horiz-origin-x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String horizOriginX;

    @XmlAttribute(name = "horiz-origin-y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String horizOriginY;

    @XmlAttribute(name = "horiz-adv-x", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String horizAdvX;

    @XmlAttribute(name = "vert-origin-x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String vertOriginX;

    @XmlAttribute(name = "vert-origin-y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String vertOriginY;

    @XmlAttribute(name = "vert-adv-y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String vertAdvY;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "font-face", type = SvgFontFace.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "missing-glyph", type = SvgMissingGlyph.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "glyph", type = SvgGlyph.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "hkern", type = SvgHorizontalKerning.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "vkern", type = SvgVerticalKerning.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgElement> content;

    @Override
    public SvgExternalResourcesAttributes getExternalResourcesAttributes() {
        return externalResources;
    }

    /**
     * Gets the value of the horizOriginX property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHorizOriginX() {
        return horizOriginX;
    }

    /**
     * Sets the value of the horizOriginX property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHorizOriginX(String value) {
        this.horizOriginX = value;
    }

    /**
     * Gets the value of the horizOriginY property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHorizOriginY() {
        return horizOriginY;
    }

    /**
     * Sets the value of the horizOriginY property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHorizOriginY(String value) {
        this.horizOriginY = value;
    }

    /**
     * Gets the value of the horizAdvX property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHorizAdvX() {
        return horizAdvX;
    }

    /**
     * Sets the value of the horizAdvX property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHorizAdvX(String value) {
        this.horizAdvX = value;
    }

    /**
     * Gets the value of the vertOriginX property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVertOriginX() {
        return vertOriginX;
    }

    /**
     * Sets the value of the vertOriginX property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVertOriginX(String value) {
        this.vertOriginX = value;
    }

    /**
     * Gets the value of the vertOriginY property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVertOriginY() {
        return vertOriginY;
    }

    /**
     * Sets the value of the vertOriginY property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVertOriginY(String value) {
        this.vertOriginY = value;
    }

    /**
     * Gets the value of the vertAdvY property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVertAdvY() {
        return vertAdvY;
    }

    /**
     * Sets the value of the vertAdvY property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVertAdvY(String value) {
        this.vertAdvY = value;
    }

    /**
     * Gets the value of the content property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgDescription }
     * {@link SvgTitle }
     * {@link SvgMetadata }
     * {@link SvgFontFace }
     * {@link SvgMissingGlyph }
     * {@link SvgGlyph }
     * {@link SvgHorizontalKerning }
     * {@link SvgVerticalKerning }
     *
     *
     */
    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

}
