package nz.co.ctg.foxglove.type;

import com.google.common.base.MoreObjects.ToStringHelper;

import static com.google.common.base.MoreObjects.toStringHelper;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Bounds;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

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

    public ViewBox(Bounds bounds) {
        this.minX = new Size(bounds.getMinX(), SizeUnits.PX);
        this.minY = new Size(bounds.getMinY(), SizeUnits.PX);
        this.width = new Size(bounds.getWidth(), SizeUnits.PX);
        this.height = new Size(bounds.getHeight(), SizeUnits.PX);
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

    /**
     * The transform that maps this viewBox onto a viewport of the given size, per the standard SVG viewBox-to-
     * viewport algorithm: scale to fit (uniformly, unless {@code preserveAspectRatio} is {@code none}), then
     * translate to align the leftover space per {@code preserveAspectRatio}.
     * <p>
     * Returns null when the viewBox has no positive area, since no transform can sensibly map it onto anything.
     */
    public Transform createTransform(double viewportWidth, double viewportHeight, PreserveAspectRatio preserveAspectRatio) {
        double vbMinX = minX == null ? 0 : minX.pixels();
        double vbMinY = minY == null ? 0 : minY.pixels();
        double vbWidth = width == null ? 0 : width.pixels();
        double vbHeight = height == null ? 0 : height.pixels();
        if (vbWidth <= 0 || vbHeight <= 0) {
            return null;
        }

        double scaleX = viewportWidth / vbWidth;
        double scaleY = viewportHeight / vbHeight;
        PreserveAspectRatio.Align align = preserveAspectRatio.getAlign();
        if (align != PreserveAspectRatio.Align.NONE) {
            double scale = preserveAspectRatio.getMeetOrSlice() == PreserveAspectRatio.MeetOrSlice.SLICE
                ? Math.max(scaleX, scaleY)
                : Math.min(scaleX, scaleY);
            scaleX = scale;
            scaleY = scale;
        }

        double translateX = -vbMinX * scaleX + align.getAlignX() * (viewportWidth - vbWidth * scaleX);
        double translateY = -vbMinY * scaleY + align.getAlignY() * (viewportHeight - vbHeight * scaleY);
        return new Affine(scaleX, 0, translateX, 0, scaleY, translateY);
    }

}
