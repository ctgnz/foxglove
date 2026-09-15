package nz.co.ctg.foxglove.shape;

import javafx.scene.shape.Shape;

import nz.co.ctg.foxglove.FxGraphic;
import nz.co.ctg.foxglove.ISvgDescribable;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgStylable;

public interface ISvgShape<S extends Shape> extends ISvgElement, ISvgDescribable, ISvgStylable, FxGraphic<S> {

}
