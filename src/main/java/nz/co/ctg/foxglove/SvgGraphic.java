package nz.co.ctg.foxglove;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.ISvgShape;

import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

@XmlRootElement(name = "svg", namespace = "http://www.w3.org/2000/svg")
public class SvgGraphic extends AbstractSvgStylable implements ISvgStylable, ISvgPresentationAttributes, ISvgGraphicsAttributes, ISvgTextAttributes,
    ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgFitToViewBox, ISvgDescribable {

    private String onUnload;
    private String onAbort;
    private String onError;
    private String onResize;
    private String onScroll;
    private String onZoom;
    private Size x;
    private Size y;
    private Size width;
    private Size height;
    private String zoomAndPan;
    private String version;
    private String baseProfile;
    private String contentScriptType;
    private String contentStyleType;
    private List<ISvgElement> content;

    public String getOnUnload() {
        return onUnload;
    }

    public void setOnUnload(String value) {
        this.onUnload = value;
    }

    public String getOnAbort() {
        return onAbort;
    }

    public void setOnAbort(String value) {
        this.onAbort = value;
    }

    public String getOnError() {
        return onError;
    }

    public void setOnError(String value) {
        this.onError = value;
    }

    public String getOnResize() {
        return onResize;
    }

    public void setOnResize(String value) {
        this.onResize = value;
    }

    public String getOnScroll() {
        return onScroll;
    }

    public void setOnScroll(String value) {
        this.onScroll = value;
    }

    public String getOnZoom() {
        return onZoom;
    }

    public void setOnZoom(String value) {
        this.onZoom = value;
    }

    public Size getX() {
        if (x == null) {
            x = new Size(0, SizeUnits.PX);
        }
        return x;
    }

    public void setX(Size value) {
        this.x = value;
    }

    public Size getY() {
        if (y == null) {
            y = new Size(0, SizeUnits.PX);
        }
        return y;
    }

    public void setY(Size value) {
        this.y = value;
    }

    public Size getWidth() {
        if (width == null) {
            width = new Size(0, SizeUnits.PX);
        }
        return width;
    }

    public void setWidth(Size value) {
        this.width = value;
    }

    public Size getHeight() {
        if (height == null) {
            height = new Size(0, SizeUnits.PX);
        }
        return height;
    }

    public void setHeight(Size value) {
        this.height = value;
    }

    public String getZoomAndPan() {
        if (zoomAndPan == null) {
            return "magnify";
        } else {
            return zoomAndPan;
        }
    }

    public void setZoomAndPan(String value) {
        this.zoomAndPan = value;
    }

    public String getVersion() {
        if (version == null) {
            return "1.1";
        } else {
            return version;
        }
    }

    public void setVersion(String value) {
        this.version = value;
    }

    public String getBaseProfile() {
        return baseProfile;
    }

    public void setBaseProfile(String value) {
        this.baseProfile = value;
    }

    public String getContentScriptType() {
        if (contentScriptType == null) {
            return "application/ecmascript";
        } else {
            return contentScriptType;
        }
    }

    public void setContentScriptType(String value) {
        this.contentScriptType = value;
    }

    public String getContentStyleType() {
        if (contentStyleType == null) {
            return "text/css";
        } else {
            return contentStyleType;
        }
    }

    public void setContentStyleType(String value) {
        this.contentStyleType = value;
    }

    @Override
    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    public Region createGraphic() {
        Pane pane = new Pane();
        pane.setTranslateX(getX().pixels());
        pane.setTranslateY(getY().pixels());
        pane.setMaxWidth(getWidth().pixels());
        pane.setMaxHeight(getHeight().pixels());
        content.forEach(child -> {
            if (child instanceof SvgGroup) {
                SvgGroup group = (SvgGroup) child;
                pane.getChildren().add(group.createGroup());
            }
            if (child instanceof ISvgShape<?>) {
                ISvgShape<?> shape = (ISvgShape<?>) child;
                pane.getChildren().add(shape.createShape());
            }
        });
        return pane;
    }

    public Group createGroup() {
        Group baseGroup = new Group();
        baseGroup.setTranslateX(getX().pixels());
        baseGroup.setTranslateY(getY().pixels());
        content.forEach(child -> {
            if (child instanceof SvgGroup) {
                SvgGroup group = (SvgGroup) child;
                baseGroup.getChildren().add(group.createGroup());
            }
            if (child instanceof ISvgShape<?>) {
                ISvgShape<?> shape = (ISvgShape<?>) child;
                baseGroup.getChildren().add(shape.createShape());
            }
        });
        return baseGroup;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("x", x);
        builder.add("y", y);
        builder.add("width", width);
        builder.add("height", height);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgFitToViewBox.super.toStringDetail(builder);
    }

}
