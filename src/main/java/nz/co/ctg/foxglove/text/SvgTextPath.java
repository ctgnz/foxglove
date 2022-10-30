package nz.co.ctg.foxglove.text;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgValueElement;
import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;
import nz.co.ctg.foxglove.attributes.SvgEventListener;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;

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
    "conditionalFeatures", "linkable", "externalResources", "eventListener", "value"
})
@XmlRootElement(name = "textPath")
public class SvgTextPath extends AbstractSvgStylable implements ISvgTextContentElement, ISvgLinkable, ISvgValueElement {

    @XmlAttribute(name = "startOffset")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String startOffset;

    @XmlAttribute(name = "textLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String textLength;

    @XmlAttribute(name = "lengthAdjust")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String lengthAdjust;

    @XmlAttribute(name = "method")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String method;

    @XmlAttribute(name = "spacing")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String spacing;

    @XmlPath(".")
    private final SvgConditionalFeaturesAttributes conditionalFeatures = new SvgConditionalFeaturesAttributes();

    @XmlPath(".")
    private final SvgLinkableAttributes linkable = new SvgLinkableAttributes();

    @XmlPath(".")
    private final SvgExternalResourcesAttributes externalResources = new SvgExternalResourcesAttributes();

    @XmlPath(".")
    private final SvgEventListener eventListener = new SvgEventListener();

    @XmlValueExtension
    private String value;

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

    @Override
    public SvgConditionalFeaturesAttributes getConditionalFeaturesAttributes() {
        return conditionalFeatures;
    }

    @Override
    public SvgLinkableAttributes getLinkableAttributes() {
        return linkable;
    }

    @Override
    public SvgExternalResourcesAttributes getExternalResourcesAttributes() {
        return externalResources;
    }

    @Override
    public SvgEventListener getEventListener() {
        return eventListener;
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
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("horizOriginX", startOffset);
        builder.add("textLength", textLength);
        builder.add("lengthAdjust", lengthAdjust);
        builder.add("method", method);
        builder.add("spacing", spacing);
        super.toStringDetail(builder);
        conditionalFeatures.toStringDetail(builder);
        linkable.toStringDetail(builder);
        externalResources.toStringDetail(builder);
        eventListener.toStringDetail(builder);
        builder.add("value", value);
    }

}
