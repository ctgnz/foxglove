package nz.co.ctg.foxglove.element;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Translate;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgBounded;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.RenderContext;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rawContent"
})
@XmlRootElement(name = "foreignObject")
public class SvgForeignObject extends AbstractSvgStylable implements ISvgStructuralElement, ISvgBounded, ISvgEventListener, ISvgConditionalFeatures, ISvgExternalResources, ISvgTransformable, FxGraphic<Node> {

    /**
     * The foreign content verbatim - almost always XHTML, captured as a real DOM subtree ({@code @XmlAnyElement} handles any namespace this binding has no class for) rather than
     * {@code @XmlValue}, which only ever captures bare text and would be empty for any realistic {@code <foreignObject>} - verified empirically before settling on this.
     */
    @XmlAnyElement
    private org.w3c.dom.Element rawContent;

    public org.w3c.dom.Element getRawContent() {
        return rawContent;
    }

    /**
     * Delegates to {@link RenderContext#getForeignObjectHandler}, handing it this element's raw content and resolved width/height and embedding whatever {@link Node} it returns.
     * No handler registered, or the handler returns {@code null}, renders an empty but correctly positioned {@link Group} - this library has no HTML engine of its own to fall back
     * on, and {@link #getRawContent} remains available either way, so content is never silently unaccounted for even when nothing is drawn for it.
     */
    @Override
    public Node createGraphic(RenderContext context) {
        applyStyle(context);
        double width = resolveWidth(context);
        double height = resolveHeight(context);
        Node content = context.getForeignObjectHandler()
            .map(handler -> handler.render(this, width, height))
            .orElse(null);

        Group group = new Group();
        group.setId(getId());
        if (content != null) {
            group.getChildren()
                .add(content);
        }
        applyNodeProperties(context, group);
        applyClip(context, group);
        applyFilter(context, group);
        group.getTransforms()
            .addAll(getTransformList());
        group.getTransforms()
            .add(new Translate(resolveX(context), resolveY(context)));
        return group;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        ISvgBounded.super.toStringDetail(builder);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
    }

}
