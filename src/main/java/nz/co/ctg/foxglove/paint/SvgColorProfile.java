package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;
import nz.co.ctg.foxglove.description.ISvgDescriptiveElement;
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
    "linkable", "content"
})
@XmlRootElement(name = "color-profile")
public class SvgColorProfile extends AbstractSvgElement implements ISvgElement, ISvgLinkable {

    @XmlAttribute(name = "local")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String local;

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String name;

    @XmlAttribute(name = "rendering-intent")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String renderingIntent;

    @XmlPath(".")
    protected final SvgLinkableAttributes linkable = new SvgLinkableAttributes();

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgDescriptiveElement> content;

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

    @Override
    public SvgLinkableAttributes getLinkableAttributes() {
        return linkable;
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
    public List<ISvgDescriptiveElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
    }

}
