package nz.co.ctg.foxglove;

import java.util.Optional;

import com.google.common.base.MoreObjects.ToStringHelper;

import javafx.css.Size;
import javafx.css.SizeUnits;

public interface ISvgBounded extends ISvgAttributes {
    String BOUNDS_X = "x";
    String BOUNDS_Y = "y";
    String BOUNDS_WIDTH = "width";
    String BOUNDS_HEIGHT = "height";

    default double getPixelsX() {
        return Optional.ofNullable(getX()).map(Size::pixels).orElse(0.0);
    }

    default double getPixelsY() {
        return Optional.ofNullable(getY()).map(Size::pixels).orElse(0.0);
    }

    default double getPixelsWidth() {
        return Optional.ofNullable(getWidth()).map(Size::pixels).orElse(0.0);
    }

    default double getPixelsHeight() {
        return Optional.ofNullable(getHeight()).map(Size::pixels).orElse(0.0);
    }

    default void setPixelsX(double xPixels) {
        setX(new Size(xPixels, SizeUnits.PX));
    }

    default void setPixelsY(double yPixels) {
        setY(new Size(yPixels, SizeUnits.PX));
    }

    default void setPixelsWidth(double widthPixels) {
        setWidth(new Size(widthPixels, SizeUnits.PX));
    }

    default void setPixelsHeight(double heightPixels) {
        setHeight(new Size(heightPixels, SizeUnits.PX));
    }

    default Size getX() {
        return get(BOUNDS_X);
    }

    default void setX(Size value) {
        set(BOUNDS_X, value);
    }

    default Size getY() {
        return get(BOUNDS_Y);
    }

    default void setY(Size value) {
        set(BOUNDS_Y, value);
    }

    default Size getWidth() {
        return get(BOUNDS_WIDTH);
    }

    default void setWidth(Size value) {
        set(BOUNDS_WIDTH, value);
    }

    default Size getHeight() {
        return get(BOUNDS_HEIGHT);
    }

    default void setHeight(Size value) {
        set(BOUNDS_HEIGHT, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(BOUNDS_X, getX());
        builder.add(BOUNDS_Y, getY());
        builder.add(BOUNDS_WIDTH, getWidth());
        builder.add(BOUNDS_HEIGHT, getHeight());
    }

}
