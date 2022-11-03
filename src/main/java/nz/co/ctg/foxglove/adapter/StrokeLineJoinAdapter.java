package nz.co.ctg.foxglove.adapter;

import javafx.scene.shape.StrokeLineJoin;

public class StrokeLineJoinAdapter extends SvgEnumAdapter<StrokeLineJoin> {

    public StrokeLineJoinAdapter() {
        super(StrokeLineJoin.class, StrokeLineJoin.MITER);
    }

}
