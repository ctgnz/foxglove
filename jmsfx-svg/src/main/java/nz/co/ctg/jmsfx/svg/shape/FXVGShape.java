package nz.co.ctg.jmsfx.svg.shape;

import javafx.scene.shape.Shape;

public interface FXVGShape<S extends Shape> {

    S createShape();

}