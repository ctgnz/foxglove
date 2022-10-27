package nz.co.ctg.foxglove.shape;

import javafx.scene.shape.Shape;

public interface ISvgShape<S extends Shape> {

    S createShape();

}
