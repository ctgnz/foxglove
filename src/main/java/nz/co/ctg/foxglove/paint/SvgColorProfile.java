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

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgLinkable;
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
@XmlRootElement(name = "color-profile")
public class SvgColorProfile extends AbstractSvgElement implements ISvgElement, ISvgLinkable {

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

    @XmlAttribute(name = "local")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String local;

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String name;

    @XmlAttribute(name = "rendering-intent")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String renderingIntent;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class),
        @XmlElement(name = "title", type = SvgTitle.class),
        @XmlElement(name = "metadata", type = SvgMetadata.class)
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
     * Gets the value of the local property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocal() {
        return local;
    }

    /**
     * Sets the value of the local property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocal(String value) {
        this.local = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the renderingIntent property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRenderingIntent() {
        if (renderingIntent == null) {
            return "auto";
        } else {
            return renderingIntent;
        }
    }

    /**
     * Sets the value of the renderingIntent property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRenderingIntent(String value) {
        this.renderingIntent = value;
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
     *
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        // TODO Auto-generated method stub

    }

}
