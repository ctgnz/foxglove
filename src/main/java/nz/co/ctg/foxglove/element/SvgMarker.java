package nz.co.ctg.foxglove.element;

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

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.animate.SvgAnimate;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSet;
import nz.co.ctg.foxglove.clip.SvgClipPath;
import nz.co.ctg.foxglove.clip.SvgMask;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.filter.Filter;
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
import nz.co.ctg.foxglove.text.AltGlyphDef;
import nz.co.ctg.foxglove.text.Font;
import nz.co.ctg.foxglove.text.FontFace;
import nz.co.ctg.foxglove.text.SvgText;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "marker")
public class SvgMarker extends AbstractSvgStylable implements ISvgExternalResources {

    @XmlAttribute(name = "externalResourcesRequired")
    protected boolean externalResourcesRequired;

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
        @XmlElement(name = "desc", type = SvgDescription.class),
        @XmlElement(name = "title", type = SvgTitle.class),
        @XmlElement(name = "metadata", type = SvgMetadata.class),
        @XmlElement(name = "animate", type = SvgAnimate.class),
        @XmlElement(name = "set", type = SvgSet.class),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class),
        @XmlElement(name = "svg", type = SvgGraphic.class),
        @XmlElement(name = "g", type = SvgGroup.class),
        @XmlElement(name = "defs", type = SvgDefs.class),
        @XmlElement(name = "symbol", type = SvgSymbol.class),
        @XmlElement(name = "use", type = SvgUse.class),
        @XmlElement(name = "switch", type = SvgSwitch.class),
        @XmlElement(name = "image", type = SvgImage.class),
        @XmlElement(name = "style", type = SvgStyle.class),
        @XmlElement(name = "path", type = SvgPath.class),
        @XmlElement(name = "rect", type = SvgRectangle.class),
        @XmlElement(name = "circle", type = SvgCircle.class),
        @XmlElement(name = "line", type = SvgLine.class),
        @XmlElement(name = "ellipse", type = SvgEllipse.class),
        @XmlElement(name = "polyline", type = SvgPolyline.class),
        @XmlElement(name = "polygon", type = SvgPolygon.class),
        @XmlElement(name = "text", type = SvgText.class),
        @XmlElement(name = "altGlyphDef", type = AltGlyphDef.class),
        @XmlElement(name = "marker", type = SvgMarker.class),
        @XmlElement(name = "color-profile", type = SvgColorProfile.class),
        @XmlElement(name = "linearGradient", type = SvgLinearGradient.class),
        @XmlElement(name = "radialGradient", type = SvgRadialGradient.class),
        @XmlElement(name = "pattern", type = SvgPattern.class),
        @XmlElement(name = "clipPath", type = SvgClipPath.class),
        @XmlElement(name = "mask", type = SvgMask.class),
        @XmlElement(name = "filter", type = Filter.class),
        @XmlElement(name = "cursor", type = SvgCursor.class),
        @XmlElement(name = "a", type = SvgAnchor.class),
        @XmlElement(name = "view", type = SvgView.class),
        @XmlElement(name = "script", type = SvgScript.class),
        @XmlElement(name = "font", type = Font.class),
        @XmlElement(name = "font-face", type = FontFace.class),
        @XmlElement(name = "foreignObject", type = SvgForeignObject.class)
    })
    protected List<Object> content;

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
     * {@link SvgAnimate }
     * {@link SvgSet }
     * {@link SvgAnimateMotion }
     * {@link SvgAnimateColor }
     * {@link SvgAnimateTransform }
     * {@link SvgGraphic }
     * {@link SvgGroup }
     * {@link SvgDefs }
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
     * {@link AltGlyphDef }
     * {@link SvgMarker }
     * {@link SvgColorProfile }
     * {@link SvgLinearGradient }
     * {@link SvgRadialGradient }
     * {@link SvgPattern }
     * {@link SvgClipPath }
     * {@link SvgMask }
     * {@link Filter }
     * {@link SvgCursor }
     * {@link SvgAnchor }
     * {@link SvgView }
     * {@link SvgScript }
     * {@link Font }
     * {@link FontFace }
     * {@link SvgForeignObject }
     *
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

}
