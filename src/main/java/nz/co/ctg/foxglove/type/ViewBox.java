package nz.co.ctg.foxglove.type;

import com.google.common.base.MoreObjects.ToStringHelper;

import static com.google.common.base.MoreObjects.toStringHelper;

import javafx.css.Size;

public class ViewBox {
    private Size minX;
    private Size minY;
    private Size width;
    private Size height;

    public ViewBox() {
    }

    public ViewBox(Size minX, Size minY, Size width, Size height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    public Size getMinX() {
        return minX;
    }

    public void setMinX(Size minX) {
        this.minX = minX;
    }

    public Size getMinY() {
        return minY;
    }

    public void setMinY(Size minY) {
        this.minY = minY;
    }

    public Size getWidth() {
        return width;
    }

    public void setWidth(Size width) {
        this.width = width;
    }

    public Size getHeight() {
        return height;
    }

    public void setHeight(Size height) {
        this.height = height;
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper("viewBox").omitNullValues();
        builder.add("minX", minX);
        builder.add("miny", minY);
        builder.add("width", width);
        builder.add("height", height);
        return builder.toString();
    }

}
