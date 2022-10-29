package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "linearGradient")
public class SvgLinearGradient extends AbstractSvgStylable implements ISvgStylable, ISvgLinkable, ISvgExternalResources {

    @XmlAttribute(name = "xmlns:xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlnsXlink;

    @XmlAttribute(name = "xlink:type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkType;

    @XmlAttribute(name = "xlink:href")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkHref;

    @XmlAttribute(name = "xlink:role")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkRole;

    @XmlAttribute(name = "xlink:arcrole")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkArcrole;

    @XmlAttribute(name = "xlink:title")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkTitle;

    @XmlAttribute(name = "xlink:show")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkShow;

    @XmlAttribute(name = "xlink:actuate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkActuate;

    @XmlAttribute(name = "externalResourcesRequired")
    protected boolean externalResourcesRequired;

    @XmlAttribute(name = "x1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String x1;

    @XmlAttribute(name = "y1")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String y1;

    @XmlAttribute(name = "x2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String x2;

    @XmlAttribute(name = "y2")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String y2;

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
        @XmlElement(name = "desc", type = SvgDescription.class),
        @XmlElement(name = "title", type = SvgTitle.class),
        @XmlElement(name = "metadata", type = SvgMetadata.class),
        @XmlElement(name = "stop", type = SvgStop.class),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class),
        @XmlElement(name = "set", type = SvgSetAttribute.class),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class)
    })
    protected List<Object> content;

    /**
     * Gets the value of the xmlnsXlink property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXmlnsXlink() {
        if (xmlnsXlink == null) {
            return "http://www.w3.org/1999/xlink";
        } else {
            return xmlnsXlink;
        }
    }

    /**
     * Sets the value of the xmlnsXlink property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXmlnsXlink(String value) {
        this.xmlnsXlink = value;
    }

    /**
     * Gets the value of the xlinkType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXlinkType() {
        if (xlinkType == null) {
            return "simple";
        } else {
            return xlinkType;
        }
    }

    /**
     * Sets the value of the xlinkType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXlinkType(String value) {
        this.xlinkType = value;
    }

    /**
     * Gets the value of the xlinkHref property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXlinkHref() {
        return xlinkHref;
    }

    /**
     * Sets the value of the xlinkHref property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXlinkHref(String value) {
        this.xlinkHref = value;
    }

    /**
     * Gets the value of the xlinkRole property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXlinkRole() {
        return xlinkRole;
    }

    /**
     * Sets the value of the xlinkRole property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXlinkRole(String value) {
        this.xlinkRole = value;
    }

    /**
     * Gets the value of the xlinkArcrole property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXlinkArcrole() {
        return xlinkArcrole;
    }

    /**
     * Sets the value of the xlinkArcrole property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXlinkArcrole(String value) {
        this.xlinkArcrole = value;
    }

    /**
     * Gets the value of the xlinkTitle property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXlinkTitle() {
        return xlinkTitle;
    }

    /**
     * Sets the value of the xlinkTitle property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXlinkTitle(String value) {
        this.xlinkTitle = value;
    }

    /**
     * Gets the value of the xlinkShow property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXlinkShow() {
        if (xlinkShow == null) {
            return "other";
        } else {
            return xlinkShow;
        }
    }

    /**
     * Sets the value of the xlinkShow property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXlinkShow(String value) {
        this.xlinkShow = value;
    }

    /**
     * Gets the value of the xlinkActuate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getXlinkActuate() {
        if (xlinkActuate == null) {
            return "onLoad";
        } else {
            return xlinkActuate;
        }
    }

    /**
     * Sets the value of the xlinkActuate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setXlinkActuate(String value) {
        this.xlinkActuate = value;
    }

    /**
     * Gets the value of the externalResourcesRequired property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public boolean getExternalResourcesRequired() {
        return externalResourcesRequired;
    }

    /**
     * Sets the value of the externalResourcesRequired property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setExternalResourcesRequired(boolean value) {
        this.externalResourcesRequired = value;
    }

    /**
     * Gets the value of the x1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getX1() {
        return x1;
    }

    /**
     * Sets the value of the x1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setX1(String value) {
        this.x1 = value;
    }

    /**
     * Gets the value of the y1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getY1() {
        return y1;
    }

    /**
     * Sets the value of the y1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setY1(String value) {
        this.y1 = value;
    }

    /**
     * Gets the value of the x2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getX2() {
        return x2;
    }

    /**
     * Sets the value of the x2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setX2(String value) {
        this.x2 = value;
    }

    /**
     * Gets the value of the y2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getY2() {
        return y2;
    }

    /**
     * Sets the value of the y2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setY2(String value) {
        this.y2 = value;
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
    public List<Object> getContent() {
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
