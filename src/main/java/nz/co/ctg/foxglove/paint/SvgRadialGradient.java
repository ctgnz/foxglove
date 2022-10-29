package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;
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
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "linkable", "externalResources", "content"
})
@XmlRootElement(name = "radialGradient")
public class SvgRadialGradient extends AbstractSvgStylable implements ISvgGradientElement {

    @XmlPath(".")
    protected final SvgLinkableAttributes linkable = new SvgLinkableAttributes();

    @XmlPath(".")
    protected final SvgExternalResourcesAttributes externalResources = new SvgExternalResourcesAttributes();

    @XmlAttribute(name = "cx")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String cx;

    @XmlAttribute(name = "cy")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String cy;

    @XmlAttribute(name = "r")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String r;

    @XmlAttribute(name = "fx")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fx;

    @XmlAttribute(name = "fy")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fy;

    @XmlAttribute(name = "gradientUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String gradientUnits;

    @XmlAttribute(name = "gradientTransform")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String gradientTransform;

    @XmlAttribute(name = "spreadMethod")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String spreadMethod;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "stop", type = SvgStop.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgElement> content;

    @Override
    public SvgLinkableAttributes getLinkableAttributes() {
        return linkable;
    }

    @Override
    public SvgExternalResourcesAttributes getExternalResourcesAttributes() {
        return externalResources;
    }

    /**
     * Gets the value of the cx property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCx() {
        return cx;
    }

    /**
     * Sets the value of the cx property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCx(String value) {
        this.cx = value;
    }

    /**
     * Gets the value of the cy property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCy() {
        return cy;
    }

    /**
     * Sets the value of the cy property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCy(String value) {
        this.cy = value;
    }

    /**
     * Gets the value of the r property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getR() {
        return r;
    }

    /**
     * Sets the value of the r property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setR(String value) {
        this.r = value;
    }

    /**
     * Gets the value of the fx property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFx() {
        return fx;
    }

    /**
     * Sets the value of the fx property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFx(String value) {
        this.fx = value;
    }

    /**
     * Gets the value of the fy property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFy() {
        return fy;
    }

    /**
     * Sets the value of the fy property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFy(String value) {
        this.fy = value;
    }

    /**
     * Gets the value of the gradientUnits property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGradientUnits() {
        return gradientUnits;
    }

    /**
     * Sets the value of the gradientUnits property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGradientUnits(String value) {
        this.gradientUnits = value;
    }

    /**
     * Gets the value of the gradientTransform property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGradientTransform() {
        return gradientTransform;
    }

    /**
     * Sets the value of the gradientTransform property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGradientTransform(String value) {
        this.gradientTransform = value;
    }

    /**
     * Gets the value of the spreadMethod property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpreadMethod() {
        return spreadMethod;
    }

    /**
     * Sets the value of the spreadMethod property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpreadMethod(String value) {
        this.spreadMethod = value;
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
     * {@link SvgStop }
     * {@link SvgAnimateAttribute }
     * {@link SvgSetAttribute }
     * {@link SvgAnimateTransform }
     *
     *
     */
    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        // TODO Auto-generated method stub

    }

}
