package nz.co.ctg.foxglove.shape;

import nz.co.ctg.foxglove.ISvgDescribable;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgStylable;

import javafx.scene.shape.Shape;

public interface ISvgShape<S extends Shape> extends ISvgElement, ISvgDescribable, ISvgStylable {

    S createGraphic();

}
