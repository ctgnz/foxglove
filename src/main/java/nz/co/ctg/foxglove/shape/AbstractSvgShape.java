package nz.co.ctg.foxglove.shape;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.ISvgTransformable;

import javafx.scene.shape.Shape;

public abstract class AbstractSvgShape<S extends Shape> extends AbstractSvgStylable
    implements ISvgShape<S>, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    @Override
    public S createGraphic(ISvgStylable parent) {
        parseStyle();
        S shape = createShape();
        shape.setId(getId());
        installTooltip(shape);
        applyGraphicsProperties(parent, shape);
        applyTransforms(shape);
        return shape;
    }

    protected abstract S createShape();

    @Override
    public void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgTransformable.super.toStringDetail(builder);
    }

}
