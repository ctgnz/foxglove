package nz.co.ctg.foxglove.shape;

import nz.co.ctg.foxglove.ISvgElement;

import javafx.scene.shape.Shape;

public interface ISvgShape<S extends Shape> extends ISvgElement {

    S createShape();

}
