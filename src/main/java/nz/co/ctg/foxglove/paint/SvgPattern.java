package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgBounded;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgFitToViewBox;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.SvgStyle;
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
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgEllipse;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgPolygon;
import nz.co.ctg.foxglove.shape.SvgPolyline;
import nz.co.ctg.foxglove.shape.SvgRectangle;
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


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "pattern")
public class SvgPattern extends AbstractSvgStylable implements ISvgBounded, ISvgConditionalFeatures, ISvgLinkable, ISvgExternalResources, ISvgFitToViewBox {

    @XmlAttribute(name = "patternUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String patternUnits;

    @XmlAttribute(name = "patternContentUnits")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String patternContentUnits;

    @XmlAttribute(name = "patternTransform")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String patternTransform;

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
    private List<ISvgElement> content;

    public String getPatternUnits() {
        return patternUnits;
    }

    public void setPatternUnits(String value) {
        this.patternUnits = value;
    }

    public String getPatternContentUnits() {
        return patternContentUnits;
    }

    public void setPatternContentUnits(String value) {
        this.patternContentUnits = value;
    }

    public String getPatternTransform() {
        return patternTransform;
    }

    public void setPatternTransform(String value) {
        this.patternTransform = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        ISvgBounded.super.toStringDetail(builder);
        builder.add("patternUnits", patternUnits);
        builder.add("patternContentUnits", patternContentUnits);
        builder.add("patternTransform", patternTransform);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgFitToViewBox.super.toStringDetail(builder);
    }

}
