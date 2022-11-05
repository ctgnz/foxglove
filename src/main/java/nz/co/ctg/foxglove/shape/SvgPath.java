package nz.co.ctg.foxglove.shape;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
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
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.shape.SVGPath;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "path", propOrder = {
    "content"
})
@XmlRootElement(name = "path")
public class SvgPath extends AbstractSvgStylable implements ISvgShape<SVGPath>, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    @XmlAttribute(name = "d", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String pathData;

    @XmlAttribute(name = "pathLength")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String pathLength;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"), @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateMotion", type = SvgAnimateMotion.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "animateTransform", type = SvgAnimateTransform.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    @Override
    public SVGPath createShape() {
        parseStyle();
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(pathData);
        applyGraphicsProperties(svgPath);
        applyTransforms(svgPath);
        return svgPath;
    }

    public String getD() {
        return pathData;
    }

    public String getPathLength() {
        return pathLength;
    }

    public void setD(String value) {
        this.pathData = value;
    }

    public void setPathLength(String value) {
        this.pathLength = value;
    }

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("pathLength", pathLength);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
        builder.add("d", pathData);
    }

}
