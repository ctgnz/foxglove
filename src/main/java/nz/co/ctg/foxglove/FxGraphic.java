package nz.co.ctg.foxglove;

import javafx.scene.Node;

public interface FxGraphic<T extends Node> {

    T createGraphic(ISvgStylable parent);
}
