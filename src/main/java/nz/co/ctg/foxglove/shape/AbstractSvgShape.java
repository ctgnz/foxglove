package nz.co.ctg.foxglove.shape;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "conditionalFeatures", "externalResources", "content"
})
public abstract class AbstractSvgShape extends AbstractSvgStylable implements ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    public static final int maxLen = 10;

    @XmlPath(".")
    protected final SvgConditionalFeaturesAttributes conditionalFeatures = new SvgConditionalFeaturesAttributes();

    @XmlPath(".")
    protected final SvgExternalResourcesAttributes externalResources = new SvgExternalResourcesAttributes();

    @XmlAttribute(name = "onfocusin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onFocusIn;

    @XmlAttribute(name = "onfocusout")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onFocusOut;

    @XmlAttribute(name = "onactivate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onActivate;

    @XmlAttribute(name = "onclick")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onClick;

    @XmlAttribute(name = "onmousedown")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseDown;

    @XmlAttribute(name = "onmouseup")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseUp;

    @XmlAttribute(name = "onmouseover")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseOver;

    @XmlAttribute(name = "onmousemove")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseMove;

    @XmlAttribute(name = "onmouseout")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseOut;

    @XmlAttribute(name = "onload")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onLoad;

    @XmlAttribute(name = "transform")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String transform;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgElement> content;

    public AbstractSvgShape() {
    }

    @Override
    public SvgConditionalFeaturesAttributes getConditionalFeaturesAttributes() {
        return conditionalFeatures;
    }

    @Override
    public SvgExternalResourcesAttributes getExternalResourcesAttributes() {
        return externalResources;
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
        return onFocusIn;
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
        this.onFocusIn = value;
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
        return onFocusOut;
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
        this.onFocusOut = value;
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
        return onActivate;
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
        this.onActivate = value;
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
        return onClick;
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
        this.onClick = value;
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
        return onMouseDown;
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
        this.onMouseDown = value;
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
        return onMouseUp;
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
        this.onMouseUp = value;
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
        return onMouseOver;
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
        this.onMouseOver = value;
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
        return onMouseMove;
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
        this.onMouseMove = value;
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
        return onMouseOut;
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
        this.onMouseOut = value;
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
        return onLoad;
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
        this.onLoad = value;
    }

    /**
     * Gets the value of the transform property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getTransform() {
        return transform;
    }

    /**
     * Sets the value of the transform property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setTransform(String value) {
        this.transform = value;
    }

    /**
     * Gets the value of the descOrTitleOrMetadataOrAnimateOrSetOrAnimateMotionOrAnimateColorOrAnimateTransform property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descOrTitleOrMetadataOrAnimateOrSetOrAnimateMotionOrAnimateColorOrAnimateTransform property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescOrTitleOrMetadataOrAnimateOrSetOrAnimateMotionOrAnimateColorOrAnimateTransform().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgDescription }
     * {@link SvgTitle }
     * {@link SvgMetadata }
     * {@link SvgAnimateAttribute }
     * {@link SvgSetAttribute }
     * {@link SvgAnimateMotion }
     * {@link SvgAnimateColor }
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
    public SvgGraphic getOwnerSVGElement() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ISvgElement getViewportElement() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CSSValue getPresentationAttribute(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CSSStyleDeclaration getStyleDeclaration() {
        // TODO Auto-generated method stub
        return null;
    }

    protected void setTransforms(Shape shape) {
        List<Transform> transformList = getTransformList();
        shape.getTransforms().addAll(transformList);
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        builder.add("requiredFeatures", getRequiredFeatures());
        builder.add("requiredExtensions", getRequiredExtensions());
        builder.add("systemLanguage", getSystemLanguage());
        builder.add("onfocusin", onFocusIn);
        builder.add("onfocusout", onFocusOut);
        builder.add("onactivate", onActivate);
        builder.add("onclick", onClick);
        builder.add("onmousedown", onMouseDown);
        builder.add("onmouseup", onMouseUp);
        builder.add("onmouseover", onMouseOver);
        builder.add("onmousemove", onMouseMove);
        builder.add("onmouseout", onMouseOut);
        builder.add("onload", onLoad);
        builder.add("externalResourcesRequired", isExternalResourcesRequired());
        builder.add("transform", transform);
        if (content != null) {
            builder.add("content", content.subList(0, Math.min(content.size(), maxLen)));
        }
    }
}