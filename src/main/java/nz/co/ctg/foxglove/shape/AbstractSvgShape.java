package nz.co.ctg.foxglove.shape;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgTransformable;

import javafx.scene.shape.Shape;

public abstract class AbstractSvgShape<S extends Shape> extends AbstractSvgStylable
    implements ISvgShape<S>, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    @Override
    public S createGraphic() {
        parseStyle();
        S circle = createShape();
        installTooltip(circle);
        applyGraphicsProperties(circle);
        applyTransforms(circle);
        return circle;
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
