package nz.co.ctg.foxglove.text;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgValueElement;
import nz.co.ctg.foxglove.helper.SvgExternalResources;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
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
    "externalResources", "value"
})
@XmlRootElement(name = "textPath")
public class SvgTextPath extends AbstractSvgStylable implements ISvgTextContentElement, ISvgLinkable, ISvgValueElement {

    @XmlAttribute(name = "requiredFeatures")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredFeatures;

    @XmlAttribute(name = "requiredExtensions")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredExtensions;

    @XmlAttribute(name = "systemLanguage")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String systemLanguage;

    @XmlAttribute(name = "onfocusin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onfocusin;

    @XmlAttribute(name = "onfocusout")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onfocusout;

    @XmlAttribute(name = "onactivate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onactivate;

    @XmlAttribute(name = "onclick")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onclick;

    @XmlAttribute(name = "onmousedown")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmousedown;

    @XmlAttribute(name = "onmouseup")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmouseup;

    @XmlAttribute(name = "onmouseover")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmouseover;

    @XmlAttribute(name = "onmousemove")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmousemove;

    @XmlAttribute(name = "onmouseout")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onmouseout;

    @XmlAttribute(name = "onload")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onload;

    @XmlAttribute(name = "xmlns:xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlnsXlink;

    @XmlAttribute(name = "xlink:type", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkType;

    @XmlAttribute(name = "xlink:href", required = true, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkHref;

    @XmlAttribute(name = "xlink:role", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkRole;

    @XmlAttribute(name = "xlink:arcrole", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkArcrole;

    @XmlAttribute(name = "xlink:title", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkTitle;

    @XmlAttribute(name = "xlink:show", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkShow;

    @XmlAttribute(name = "xlink:actuate", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkActuate;

    @XmlPath(".")
    protected final SvgExternalResources externalResources = new SvgExternalResources();

    @XmlAttribute(name = "startOffset")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String startOffset;

    @XmlAttribute(name = "textLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String textLength;

    @XmlAttribute(name = "lengthAdjust")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String lengthAdjust;

    @XmlAttribute(name = "method")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String method;

    @XmlAttribute(name = "spacing")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String spacing;

    @XmlValueExtension
    protected String value;

    /**
     * Gets the value of the requiredFeatures property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getRequiredFeatures() {
        return requiredFeatures;
    }

    /**
     * Sets the value of the requiredFeatures property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setRequiredFeatures(String value) {
        this.requiredFeatures = value;
    }

    /**
     * Gets the value of the requiredExtensions property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getRequiredExtensions() {
        return requiredExtensions;
    }

    /**
     * Sets the value of the requiredExtensions property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setRequiredExtensions(String value) {
        this.requiredExtensions = value;
    }

    /**
     * Gets the value of the systemLanguage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getSystemLanguage() {
        return systemLanguage;
    }

    /**
     * Sets the value of the systemLanguage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setSystemLanguage(String value) {
        this.systemLanguage = value;
    }

    /**
     * Gets the value of the onfocusin property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnFocusIn() {
        return onfocusin;
    }

    /**
     * Sets the value of the onfocusin property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnFocusIn(String value) {
        this.onfocusin = value;
    }

    /**
     * Gets the value of the onfocusout property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnFocusOut() {
        return onfocusout;
    }

    /**
     * Sets the value of the onfocusout property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnFocusOut(String value) {
        this.onfocusout = value;
    }

    /**
     * Gets the value of the onactivate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnActivate() {
        return onactivate;
    }

    /**
     * Sets the value of the onactivate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnActivate(String value) {
        this.onactivate = value;
    }

    /**
     * Gets the value of the onclick property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnClick() {
        return onclick;
    }

    /**
     * Sets the value of the onclick property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnClick(String value) {
        this.onclick = value;
    }

    /**
     * Gets the value of the onmousedown property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnMouseDown() {
        return onmousedown;
    }

    /**
     * Sets the value of the onmousedown property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnMouseDown(String value) {
        this.onmousedown = value;
    }

    /**
     * Gets the value of the onmouseup property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnMouseUp() {
        return onmouseup;
    }

    /**
     * Sets the value of the onmouseup property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnMouseUp(String value) {
        this.onmouseup = value;
    }

    /**
     * Gets the value of the onmouseover property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnMouseOver() {
        return onmouseover;
    }

    /**
     * Sets the value of the onmouseover property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnMouseOver(String value) {
        this.onmouseover = value;
    }

    /**
     * Gets the value of the onmousemove property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnMouseMove() {
        return onmousemove;
    }

    /**
     * Sets the value of the onmousemove property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnMouseMove(String value) {
        this.onmousemove = value;
    }

    /**
     * Gets the value of the onmouseout property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnMouseOut() {
        return onmouseout;
    }

    /**
     * Sets the value of the onmouseout property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnMouseOut(String value) {
        this.onmouseout = value;
    }

    /**
     * Gets the value of the onload property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getOnLoad() {
        return onload;
    }

    /**
     * Sets the value of the onload property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setOnLoad(String value) {
        this.onload = value;
    }

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
     * Gets the value of the startOffset property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartOffset() {
        return startOffset;
    }

    /**
     * Sets the value of the startOffset property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartOffset(String value) {
        this.startOffset = value;
    }

    /**
     * Gets the value of the textLength property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTextLength() {
        return textLength;
    }

    /**
     * Sets the value of the textLength property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTextLength(String value) {
        this.textLength = value;
    }

    /**
     * Gets the value of the lengthAdjust property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLengthAdjust() {
        return lengthAdjust;
    }

    /**
     * Sets the value of the lengthAdjust property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLengthAdjust(String value) {
        this.lengthAdjust = value;
    }

    /**
     * Gets the value of the method property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMethod(String value) {
        this.method = value;
    }

    /**
     * Gets the value of the spacing property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpacing() {
        return spacing;
    }

    /**
     * Sets the value of the spacing property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpacing(String value) {
        this.spacing = value;
    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public SvgExternalResources getExternalResources() {
        return externalResources;
    }

}
