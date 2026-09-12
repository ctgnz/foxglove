package nz.co.ctg.foxglove.element;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgBounded;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgContainer;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgGraphicsAttributes;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.RenderContext.Axis;
import nz.co.ctg.foxglove.SvgElementIndex;
import nz.co.ctg.foxglove.SvgGraphic;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.type.ViewBox;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import javafx.css.Size;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "use")
public class SvgUse extends AbstractSvgStylable
    implements ISvgStructuralElement, ISvgBounded, ISvgEventListener, ISvgConditionalFeatures, ISvgTransformable, ISvgLinkable, ISvgExternalResources,
    FxGraphic<Group> {

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
    private List<ISvgElement> content;

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    /**
     * Resolves {@code xlink:href} and renders the referenced element in this {@code <use>}'s place, translated by
     * its own x/y and with its own {@code transform} applied. The referenced content is handed a context resolved
     * from this element's own ancestors, so it inherits from the {@code <use>} site rather than from wherever it
     * was declared - the same mechanism paint server {@code xlink:href} inheritance already relies on.
     * <p>
     * Resolves to an empty {@link Group} - never {@code null} - when the reference is missing, invisible, or would
     * reuse one of this element's own ancestors, which the specification declares an error and which would
     * otherwise expand forever.
     * <p>
     * Per the specification, {@code translate(x,y)} is appended to the end of this element's own {@code transform}
     * list rather than applied separately - so both go into the JavaFX {@code transforms} list, in that order, and
     * neither uses the {@code translateX}/{@code translateY} node properties. Those properties are always the
     * outermost operation in JavaFX regardless of call order, which would apply this element's own {@code transform}
     * attribute - meant to wrap the translated result - before the translation instead of after it.
     */
    @Override
    public Group createGraphic(RenderContext context) {
        applyStyle(context);
        Group group = new Group();
        group.setId(getId());
        applyNodeProperties(context, group);
        applyTransforms(group);
        group.getTransforms().add(new Translate(resolveX(context), resolveY(context)));

        SvgElementIndex index = context.getElementIndex();
        RenderContext childContext = context.resolveChild(this);
        index.resolve(getXlinkHref())
            .filter(target -> ISvgContainer.isRendered(target, context.getLocale()))
            .filter(target -> !index.isSelfOrAncestor(target, this))
            .map(target -> buildReferenced(target, childContext))
            .ifPresent(node -> group.getChildren().add(node));
        applyClip(context, group);
        applyFilter(context, group);
        return group;
    }

    /**
     * Also applies the *referenced target's own* {@code mask} (independent of any {@code mask} on this {@code
     * <use>} element itself, which - like this element's own {@code clip-path} - is applied to this element's
     * returned {@code group} by whichever container consumes it, the same as any other child; see
     * {@link ISvgContainer#appendContent}). This dispatch is the one place the referenced target's own node is built
     * outside that shared consumer path, so it is the one place that has to apply the target's mask - and register
     * its node (#30) - itself.
     * <p>
     * A known limitation shared with masking above: if the same target is referenced by more than one {@code <use>},
     * only the most recently built copy stays in the node registry - an animation on content reused as a shared
     * {@code <symbol>}/template, expecting each copy to animate independently, is out of scope for now.
     */
    private Node buildReferenced(ISvgElement target, RenderContext context) {
        Node node;
        if (target instanceof SvgSymbol symbol) {
            node = buildSymbol(symbol, context);
        } else if (target instanceof SvgGraphic svg) {
            node = svg.createGraphic(context, getWidth(), getHeight());
        } else if (target instanceof FxGraphic<?> graphic) {
            node = graphic.createGraphic(context);
        } else {
            return null;
        }
        if (target instanceof ISvgGraphicsAttributes attrs) {
            node = attrs.applyMask(context, node);
            attrs.registerNode(context, node);
        }
        return node;
    }

    /**
     * Renders a {@code <symbol>} as if it were an {@code <svg>}: this {@code <use>}'s width/height establish the
     * viewport (defaulting to the current viewport size, per the specification's 100% default), and the symbol's
     * own {@code viewBox} maps onto it.
     */
    private Node buildSymbol(SvgSymbol symbol, RenderContext context) {
        symbol.applyStyle(context);
        Group group = new Group();
        group.setId(symbol.getId());

        double width = resolveDimension(getWidth(), context, Axis.HORIZONTAL);
        double height = resolveDimension(getHeight(), context, Axis.VERTICAL);
        double childViewportWidth = width;
        double childViewportHeight = height;
        ViewBox viewBox = symbol.getViewBox();
        if (viewBox != null) {
            Transform viewBoxTransform = symbol.createViewportTransform(width, height);
            if (viewBoxTransform != null) {
                group.getTransforms().add(viewBoxTransform);
            }
            if (viewBox.getWidth() != null) {
                childViewportWidth = viewBox.getWidth().pixels();
            }
            if (viewBox.getHeight() != null) {
                childViewportHeight = viewBox.getHeight().pixels();
            }
        }
        symbol.appendContent(group, context.withViewport(childViewportWidth, childViewportHeight));
        return group;
    }

    private static double resolveDimension(Size size, RenderContext context, Axis axis) {
        if (size != null) {
            return context.resolveLength(size, axis);
        }
        return axis == Axis.HORIZONTAL ? context.getViewportWidth() : context.getViewportHeight();
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        ISvgBounded.super.toStringDetail(builder);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
    }

}
