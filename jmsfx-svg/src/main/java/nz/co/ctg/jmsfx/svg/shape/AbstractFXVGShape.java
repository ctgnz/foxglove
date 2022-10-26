package nz.co.ctg.jmsfx.svg.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.jmsfx.svg.AbstractFXVGStylable;
import nz.co.ctg.jmsfx.svg.FXVGConditionalFeatures;
import nz.co.ctg.jmsfx.svg.FXVGElement;
import nz.co.ctg.jmsfx.svg.FXVGEventListener;
import nz.co.ctg.jmsfx.svg.FXVGExternalResources;
import nz.co.ctg.jmsfx.svg.FXVGSvgElement;
import nz.co.ctg.jmsfx.svg.FXVGTransformable;
import nz.co.ctg.jmsfx.svg.animate.Animate;
import nz.co.ctg.jmsfx.svg.animate.AnimateColor;
import nz.co.ctg.jmsfx.svg.animate.AnimateMotion;
import nz.co.ctg.jmsfx.svg.animate.AnimateTransform;
import nz.co.ctg.jmsfx.svg.animate.Set;
import nz.co.ctg.jmsfx.svg.description.Desc;
import nz.co.ctg.jmsfx.svg.description.Metadata;
import nz.co.ctg.jmsfx.svg.description.Title;

import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "content"
})
public abstract class AbstractFXVGShape extends AbstractFXVGStylable implements FXVGConditionalFeatures, FXVGExternalResources, FXVGEventListener, FXVGTransformable {

    protected static final int maxLen = 10;

    /* FXVGConditionalFeatures */

    @XmlAttribute(name = "requiredFeatures")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredFeatures;

    @XmlAttribute(name = "requiredExtensions")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredExtensions;

    @XmlAttribute(name = "systemLanguage")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String systemLanguage;

    /* Graphical Events */

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

    /* SVGExternalResourcesRequired */

    @XmlAttribute(name = "externalResourcesRequired")
    protected boolean externalResourcesRequired;

    /* SVGTransformable */

    @XmlAttribute(name = "transform")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String transform;

    /* Content */

    @XmlElements({
        @XmlElement(name = "desc", type = Desc.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = Title.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = Metadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = Animate.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = Set.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = AnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = AnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = AnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<Object> content;

    public AbstractFXVGShape() {
    }

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
     * {@link Desc }
     * {@link Title }
     * {@link Metadata }
     * {@link Animate }
     * {@link Set }
     * {@link AnimateMotion }
     * {@link AnimateColor }
     * {@link AnimateTransform }
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
    public String toString() {
        ToStringHelper builder = MoreObjects.toStringHelper(this).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    protected void toStringDetail(ToStringHelper builder) {
        builder.add("id", id);
        builder.add("xmlBase", xmlBase);
        builder.add("xmlLang", xmlLang);
        builder.add("xmlSpace", xmlSpace);
        builder.add("requiredFeatures", requiredFeatures);
        builder.add("requiredExtensions", requiredExtensions);
        builder.add("systemLanguage", systemLanguage);
        builder.add("style", style);
        builder.add("clazz", className);
        builder.add("enableBackground", enableBackground);
        builder.add("clip", clip);
        builder.add("overflow", overflow);
        builder.add("writingMode", writingMode);
        builder.add("alignmentBaseline", alignmentBaseline);
        builder.add("baselineShift", baselineShift);
        builder.add("direction", direction);
        builder.add("dominantBaseline", dominantBaseline);
        builder.add("glyphOrientationHorizontal", glyphOrientationHorizontal);
        builder.add("glyphOrientationVertical", glyphOrientationVertical);
        builder.add("kerning", kerning);
        builder.add("letterSpacing", letterSpacing);
        builder.add("textAnchor", textAnchor);
        builder.add("textDecoration", textDecoration);
        builder.add("unicodeBidi", unicodeBidi);
        builder.add("wordSpacing", wordSpacing);
        builder.add("fontFamily", fontFamily);
        builder.add("fontSize", fontSize);
        builder.add("fontSizeAdjust", fontSizeAdjust);
        builder.add("fontStretch", fontStretch);
        builder.add("fontStyle", fontStyle);
        builder.add("fontVariant", fontVariant);
        builder.add("fontWeight", fontWeight);
        builder.add("fill", fill);
        builder.add("fillRule", fillRule);
        builder.add("stroke", stroke);
        builder.add("strokeDasharray", strokeDashArray);
        builder.add("strokeDashoffset", strokeDashOffset);
        builder.add("strokeLinecap", strokeLineCap);
        builder.add("strokeLinejoin", strokeLineJoin);
        builder.add("strokeMiterlimit", strokeMiterLimit);
        builder.add("strokeWidth", strokeWidth);
        builder.add("color", color);
        builder.add("colorInterpolation", colorInterpolation);
        builder.add("colorRendering", colorRendering);
        builder.add("opacity", opacity);
        builder.add("fillOpacity", fillOpacity);
        builder.add("strokeOpacity", strokeOpacity);
        builder.add("display", display);
        builder.add("imageRendering", imageRendering);
        builder.add("pointerEvents", pointerEvents);
        builder.add("shapeRendering", shapeRendering);
        builder.add("textRendering", textRendering);
        builder.add("visibility", visibility);
        builder.add("markerStart", markerStart);
        builder.add("markerMid", markerMid);
        builder.add("markerEnd", markerEnd);
        builder.add("colorProfile", colorProfile);
        builder.add("stopColor", stopColor);
        builder.add("stopOpacity", stopOpacity);
        builder.add("clipPath", clipPath);
        builder.add("clipRule", clipRule);
        builder.add("mask", mask);
        builder.add("filter", filter);
        builder.add("colorInterpolationFilters", colorInterpolationFilters);
        builder.add("cursor", cursor);
        builder.add("floodColor", floodColor);
        builder.add("floodOpacity", floodOpacity);
        builder.add("lightingColor", lightingColor);
        builder.add("onfocusin", onfocusin);
        builder.add("onfocusout", onfocusout);
        builder.add("onactivate", onactivate);
        builder.add("onclick", onclick);
        builder.add("onmousedown", onmousedown);
        builder.add("onmouseup", onmouseup);
        builder.add("onmouseover", onmouseover);
        builder.add("onmousemove", onmousemove);
        builder.add("onmouseout", onmouseout);
        builder.add("onload", onload);
        builder.add("externalResourcesRequired", externalResourcesRequired);
        builder.add("transform", transform);
        if (content != null) {
            builder.add("content", content.subList(0, Math.min(content.size(), maxLen)));
        }
    }

    @Override
    public FXVGSvgElement getOwnerSVGElement() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FXVGElement getViewportElement() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CSSValue getPresentationAttribute(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Transform> getTransformList() {
        return Collections.emptyList();
    }

    @Override
    public CSSStyleDeclaration getStyleDeclaration() {
        // TODO Auto-generated method stub
        return null;
    }

    protected void setTransforms(Shape shape) {
//        shape.getTransforms().addAll(transforms);
    }

}