package nz.co.ctg.foxglove.clip;

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

import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.element.SvgAnchor;
import nz.co.ctg.foxglove.element.SvgCursor;
import nz.co.ctg.foxglove.element.SvgDefinitions;
import nz.co.ctg.foxglove.element.SvgForeignObject;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.element.SvgImage;
import nz.co.ctg.foxglove.element.SvgMarker;
import nz.co.ctg.foxglove.element.SvgScript;
import nz.co.ctg.foxglove.element.SvgSwitch;
import nz.co.ctg.foxglove.element.SvgSymbol;
import nz.co.ctg.foxglove.element.SvgUse;
import nz.co.ctg.foxglove.element.SvgView;
import nz.co.ctg.foxglove.filter.SvgFilter;
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


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "mask")
public class SvgMask extends AbstractSvgClipElement implements ISvgClipElement {

    @XmlAttribute(name = "x")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String x;

    @XmlAttribute(name = "y")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String y;

    @XmlAttribute(name = "width")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String width;

    @XmlAttribute(name = "height")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String height;

    @XmlAttribute(name = "maskUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String maskUnits;

    @XmlAttribute(name = "maskContentUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String maskContentUnits;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class),
        @XmlElement(name = "title", type = SvgTitle.class),
        @XmlElement(name = "metadata", type = SvgMetadata.class),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class),
        @XmlElement(name = "set", type = SvgSetAttribute.class),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class),
        @XmlElement(name = "svg", type = SvgGraphic.class),
        @XmlElement(name = "g", type = SvgGroup.class),
        @XmlElement(name = "defs", type = SvgDefinitions.class),
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
        @XmlElement(name = "altGlyphDef", type = SvgAltGlyphDef.class),
        @XmlElement(name = "marker", type = SvgMarker.class),
        @XmlElement(name = "color-profile", type = SvgColorProfile.class),
        @XmlElement(name = "linearGradient", type = SvgLinearGradient.class),
        @XmlElement(name = "radialGradient", type = SvgRadialGradient.class),
        @XmlElement(name = "pattern", type = SvgPattern.class),
        @XmlElement(name = "clipPath", type = SvgClipPath.class),
        @XmlElement(name = "mask", type = SvgMask.class),
        @XmlElement(name = "filter", type = SvgFilter.class),
        @XmlElement(name = "cursor", type = SvgCursor.class),
        @XmlElement(name = "a", type = SvgAnchor.class),
        @XmlElement(name = "view", type = SvgView.class),
        @XmlElement(name = "script", type = SvgScript.class),
        @XmlElement(name = "font", type = SvgFont.class),
        @XmlElement(name = "font-face", type = SvgFontFace.class),
        @XmlElement(name = "foreignObject", type = SvgForeignObject.class)
    })
    protected List<Object> content;

    /**
     * Gets the value of the x property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setX(String value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setY(String value) {
        this.y = value;
    }

    /**
     * Gets the value of the width property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Gets the value of the maskUnits property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaskUnits() {
        return maskUnits;
    }

    /**
     * Sets the value of the maskUnits property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaskUnits(String value) {
        this.maskUnits = value;
    }

    /**
     * Gets the value of the maskContentUnits property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaskContentUnits() {
        return maskContentUnits;
    }

    /**
     * Sets the value of the maskContentUnits property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaskContentUnits(String value) {
        this.maskContentUnits = value;
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
     *    content().add(newItem);
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
