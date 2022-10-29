package nz.co.ctg.foxglove.element;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgFitToViewBox;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.clip.SvgClipPath;
import nz.co.ctg.foxglove.clip.SvgMask;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.filter.SvgFilter;
import nz.co.ctg.foxglove.helper.SvgExternalResources;
import nz.co.ctg.foxglove.paint.SvgColorProfile;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.paint.SvgPattern;
import nz.co.ctg.foxglove.paint.SvgRadialGradient;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgEllipse;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgPolygon;
import nz.co.ctg.foxglove.shape.SvgPolyline;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.style.SvgStyle;
import nz.co.ctg.foxglove.text.SvgAltGlyphDef;
import nz.co.ctg.foxglove.text.SvgFont;
import nz.co.ctg.foxglove.text.SvgFontFace;
import nz.co.ctg.foxglove.text.SvgText;

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
    "externalResources", "content"
})
@XmlRootElement(name = "marker")
public class SvgMarker extends AbstractSvgStylable implements ISvgStructuralElement, ISvgStylable, ISvgFitToViewBox {

    @XmlPath(".")
    protected final SvgExternalResources externalResources = new SvgExternalResources();

    @XmlAttribute(name = "refX")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String refX;

    @XmlAttribute(name = "refY")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String refY;

    @XmlAttribute(name = "markerUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String markerUnits;

    @XmlAttribute(name = "markerWidth")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String markerWidth;

    @XmlAttribute(name = "markerHeight")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String markerHeight;

    @XmlAttribute(name = "orient")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String orient;

    @XmlAttribute(name = "viewBox")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String viewBox;

    @XmlAttribute(name = "preserveAspectRatio")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String preserveAspectRatio;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "svg", type = SvgGraphic.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "g", type = SvgGroup.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "defs", type = SvgDefinitions.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "symbol", type = SvgSymbol.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "use", type = SvgUse.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "switch", type = SvgSwitch.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "image", type = SvgImage.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "style", type = SvgStyle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "path", type = SvgPath.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "rect", type = SvgRectangle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "circle", type = SvgCircle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "line", type = SvgLine.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "ellipse", type = SvgEllipse.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "polyline", type = SvgPolyline.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "polygon", type = SvgPolygon.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "text", type = SvgText.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "altGlyphDef", type = SvgAltGlyphDef.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "marker", type = SvgMarker.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "color-profile", type = SvgColorProfile.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "linearGradient", type = SvgLinearGradient.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "radialGradient", type = SvgRadialGradient.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "pattern", type = SvgPattern.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "clipPath", type = SvgClipPath.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "mask", type = SvgMask.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "filter", type = SvgFilter.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "cursor", type = SvgCursor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "a", type = SvgAnchor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "view", type = SvgView.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "script", type = SvgScript.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "font", type = SvgFont.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "font-face", type = SvgFontFace.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "foreignObject", type = SvgForeignObject.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgElement> content;

    /**
     * Gets the value of the refX property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefX() {
        return refX;
    }

    /**
     * Sets the value of the refX property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefX(String value) {
        this.refX = value;
    }

    /**
     * Gets the value of the refY property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefY() {
        return refY;
    }

    /**
     * Sets the value of the refY property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefY(String value) {
        this.refY = value;
    }

    /**
     * Gets the value of the markerUnits property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMarkerUnits() {
        return markerUnits;
    }

    /**
     * Sets the value of the markerUnits property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMarkerUnits(String value) {
        this.markerUnits = value;
    }

    /**
     * Gets the value of the markerWidth property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMarkerWidth() {
        return markerWidth;
    }

    /**
     * Sets the value of the markerWidth property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMarkerWidth(String value) {
        this.markerWidth = value;
    }

    /**
     * Gets the value of the markerHeight property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMarkerHeight() {
        return markerHeight;
    }

    /**
     * Sets the value of the markerHeight property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMarkerHeight(String value) {
        this.markerHeight = value;
    }

    /**
     * Gets the value of the orient property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrient() {
        return orient;
    }

    /**
     * Sets the value of the orient property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrient(String value) {
        this.orient = value;
    }

    /**
     * Gets the value of the viewBox property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getViewBox() {
        return viewBox;
    }

    /**
     * Sets the value of the viewBox property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setViewBox(String value) {
        this.viewBox = value;
    }

    /**
     * Gets the value of the preserveAspectRatio property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getPreserveAspectRatio() {
        if (preserveAspectRatio == null) {
            return "xMidYMid meet";
        } else {
            return preserveAspectRatio;
        }
    }

    /**
     * Sets the value of the preserveAspectRatio property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setPreserveAspectRatio(String value) {
        this.preserveAspectRatio = value;
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
     * {@link SvgAnimateAttribute }
     * {@link SvgSetAttribute }
     * {@link SvgAnimateMotion }
     * {@link SvgAnimateColor }
     * {@link SvgAnimateTransform }
     * {@link SvgGraphic }
     * {@link SvgGroup }
     * {@link SvgDefinitions }
     * {@link SvgSymbol }
     * {@link SvgUse }
     * {@link SvgSwitch }
     * {@link SvgImage }
     * {@link SvgStyle }
     * {@link SvgPath }
     * {@link SvgRectangle }
     * {@link SvgCircle }
     * {@link SvgLine }
     * {@link SvgEllipse }
     * {@link SvgPolyline }
     * {@link SvgPolygon }
     * {@link SvgText }
     * {@link SvgAltGlyphDef }
     * {@link SvgMarker }
     * {@link SvgColorProfile }
     * {@link SvgLinearGradient }
     * {@link SvgRadialGradient }
     * {@link SvgPattern }
     * {@link SvgClipPath }
     * {@link SvgMask }
     * {@link SvgFilter }
     * {@link SvgCursor }
     * {@link SvgAnchor }
     * {@link SvgView }
     * {@link SvgScript }
     * {@link SvgFont }
     * {@link SvgFontFace }
     * {@link SvgForeignObject }
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
    public SvgExternalResources getExternalResources() {
        return externalResources;
    }

}
