package nz.co.ctg.foxglove.shape;

import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgEventListener;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgStylable;
import nz.co.ctg.foxglove.ISvgTransformable;

import javafx.scene.shape.Shape;

public interface ISvgShape<S extends Shape> extends ISvgStylable, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgTransformable {

    S createShape();

}
