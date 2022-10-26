package nz.co.ctg.jmsfx.svg.adapter;

import javafx.scene.shape.StrokeLineCap;

public class StrokeLineCapAdapter extends SvgEnumAdapter<StrokeLineCap> {

    public StrokeLineCapAdapter() {
        super(StrokeLineCap.class, StrokeLineCap.BUTT);
    }

}
