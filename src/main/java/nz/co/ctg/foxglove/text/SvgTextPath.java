package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "textPath")
public class SvgTextPath extends AbstractSvgTextContentElement implements ISvgConditionalFeatures, ISvgLinkable, ISvgExternalResources, ISvgEventListener {

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

    @XmlMixed
    @XmlElementRefs({
        @XmlElementRef(name = "tspan", type = SvgTextSpan.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "tref", type = SvgTextReference.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElementRef(name = "altGlyph", type = SvgAltGlyph.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<Object> content;

    public String getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(String value) {
        this.startOffset = value;
    }

    public String getTextLength() {
        return textLength;
    }

    public void setTextLength(String value) {
        this.textLength = value;
    }

    public String getLengthAdjust() {
        return lengthAdjust;
    }

    public void setLengthAdjust(String value) {
        this.lengthAdjust = value;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String value) {
        this.method = value;
    }

    public String getSpacing() {
        return spacing;
    }

    public void setSpacing(String value) {
        this.spacing = value;
    }

    @Override
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("horizOriginX", startOffset);
        builder.add("textLength", textLength);
        builder.add("lengthAdjust", lengthAdjust);
        builder.add("method", method);
        builder.add("spacing", spacing);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        builder.add("value", getValue());
    }

}
