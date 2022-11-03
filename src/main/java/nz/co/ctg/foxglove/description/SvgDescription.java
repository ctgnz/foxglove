package nz.co.ctg.foxglove.description;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlValueExtension;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.attributes.SvgGraphicsStyleAttributes;
import nz.co.ctg.foxglove.attributes.SvgPresentationStyleAttributes;
import nz.co.ctg.foxglove.attributes.SvgTextStyleAttributes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "presentation", "graphics", "text", "value"
})
@XmlRootElement(name = "desc")
public class SvgDescription extends AbstractSvgElement implements ISvgDescriptiveElement, ISvgStylable {

    @XmlValueExtension
    private String value;

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgPresentationStyleAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@flood-color", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@flood-opacity", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@lighting-color", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@enable-background", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@clip", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@overflow", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@marker-start", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@marker-mid", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@marker-end", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@clip-path", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@clip-rule", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@mask", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@filter", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color-interpolation-filters", transformerClass = SvgPresentationStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@cursor", transformerClass = SvgPresentationStyleAttributes.class)
    })
    private final SvgPresentationStyleAttributes presentation = new SvgPresentationStyleAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgGraphicsStyleAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@fill", transformerClass = SvgGraphicsStyleAttributes.class), @XmlWriteTransformer(xmlPath = "@fill-rule", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-dasharray", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-dashoffset", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-linecap", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-linejoin", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-miterlimit", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-width", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color-interpolation", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color-rendering", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@opacity", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@fill-opacity", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stroke-opacity", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@display", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@image-rendering", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@pointer-events", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@shape-rendering", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@text-rendering", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@visibility", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@color-profile", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stop-color", transformerClass = SvgGraphicsStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@stop-opacity", transformerClass = SvgGraphicsStyleAttributes.class)
    })
    private final SvgGraphicsStyleAttributes graphics = new SvgGraphicsStyleAttributes();

    @XmlTransformation
    @XmlReadTransformer(transformerClass = SvgTextStyleAttributes.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(xmlPath = "@font-family", transformerClass = SvgTextStyleAttributes.class), @XmlWriteTransformer(xmlPath = "@font-size", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-size-adjust", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-stretch", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-style", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-variant", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@font-weight", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@alignment-baseline", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@baseline-shift", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@direction", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@dominant-baseline", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@glyph-orientation-horizontal", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@glyph-orientation-vertical", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@kerning", transformerClass = SvgTextStyleAttributes.class), @XmlWriteTransformer(xmlPath = "@letter-spacing", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@text-anchor", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@text-decoration", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@unicode-bidi", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@word-spacing", transformerClass = SvgTextStyleAttributes.class),
        @XmlWriteTransformer(xmlPath = "@writing-mode", transformerClass = SvgTextStyleAttributes.class)
    })
    private final SvgTextStyleAttributes text = new SvgTextStyleAttributes();

    @XmlAttribute(name = "style")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String style;

    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String className;

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String value) {
        this.style = value;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String value) {
        this.className = value;
    }

    @Override
    public SvgPresentationStyleAttributes getPresentationAttributes() {
        return presentation;
    }

    @Override
    public SvgGraphicsStyleAttributes getGraphicsAttributes() {
        return graphics;
    }

    @Override
    public SvgTextStyleAttributes getTextAttributes() {
        return text;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        builder.add("style", style);
        builder.add("className", className);
        presentation.toStringDetail(builder);
        graphics.toStringDetail(builder);
        text.toStringDetail(builder);
        builder.add("value", value);
    }

}
