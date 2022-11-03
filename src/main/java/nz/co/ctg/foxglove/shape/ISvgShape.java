package nz.co.ctg.foxglove.shape;

import nz.co.ctg.foxglove.ISvgStylable;

import javafx.scene.shape.Shape;

public interface ISvgShape<S extends Shape> extends ISvgStylable {

    S createShape();

}
