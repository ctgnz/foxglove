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
import nz.co.ctg.foxglove.attributes.SvgEventListener;
import nz.co.ctg.foxglove.attributes.SvgExternalResourcesAttributes;
import nz.co.ctg.foxglove.attributes.SvgTransformAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;
import javafx.scene.shape.Shape;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "conditionalFeatures", "externalResources", "eventListener", "transform", "content"
})
public abstract class AbstractSvgShape extends AbstractSvgStylable implements ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    public static final int maxLen = 10;

    @XmlPath(".")
    protected final SvgConditionalFeaturesAttributes conditionalFeatures = new SvgConditionalFeaturesAttributes();

    @XmlPath(".")
    protected final SvgExternalResourcesAttributes externalResources = new SvgExternalResourcesAttributes();

    @XmlPath(".")
    protected final SvgEventListener eventListener = new SvgEventListener();

    @XmlPath(".")
    protected final SvgTransformAttribute transform = new SvgTransformAttribute();

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

    @Override
    public SvgEventListener getEventListener() {
        return eventListener;
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
        shape.getTransforms().addAll(transform.getTransformList());
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        conditionalFeatures.toStringDetail(builder);
        eventListener.toStringDetail(builder);
        externalResources.toStringDetail(builder);
        builder.add("transform", transform);
        if (content != null) {
            builder.add("content", content.subList(0, Math.min(content.size(), maxLen)));
        }
    }

    @Override
    public SvgTransformAttribute getTransformAttribute() {
        return transform;
    }
}