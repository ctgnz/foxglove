package nz.co.ctg.foxglove.shape;

import javafx.scene.shape.Shape;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgTransformable;
import nz.co.ctg.foxglove.RenderContext;

public abstract class AbstractSvgShape<S extends Shape> extends AbstractSvgStylable implements ISvgShape<S>, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    @Override
    public S createGraphic(RenderContext context) {
        applyStyle(context);
        S shape = createShape(context);
        shape.setId(getId());
        installTooltip(shape);
        applyGraphicsProperties(context, shape);
        applyClip(context, shape);
        applyFilter(context, shape);
        applyTransforms(shape);
        return shape;
    }

    protected abstract S createShape(RenderContext context);

    @Override
    public void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
    }

}
