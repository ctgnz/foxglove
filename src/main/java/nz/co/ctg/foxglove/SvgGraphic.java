package nz.co.ctg.foxglove;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.shape.ISvgShape;
import nz.co.ctg.foxglove.text.SvgText;

import static java.util.stream.Collectors.toList;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.geometry.Bounds;
import javafx.scene.Group;

@XmlRootElement(name = "svg", namespace = "http://www.w3.org/2000/svg")
public class SvgGraphic extends AbstractSvgStylable
    implements ISvgStylable, ISvgBounded, ISvgConditionalFeatures, ISvgExternalResources, ISvgEventListener, ISvgFitToViewBox, ISvgDescribable {

    private String onUnload;
    private String onAbort;
    private String onError;
    private String onResize;
    private String onScroll;
    private String onZoom;
    private String zoomAndPan;
    private String version;
    private String baseProfile;
    private String contentScriptType;
    private String contentStyleType;
    private List<ISvgElement> content;

    public void addContent(ISvgElement element) {
        if (element != null) {
            content.add(element);
        }
    }

    public Group createGroup() {
        Group baseGroup = new Group();
        baseGroup.setTranslateX(getPixelsX());
        baseGroup.setTranslateY(getPixelsY());
        baseGroup.setId(StringUtils.defaultIfBlank(getId(), "svg"));
        getContent().forEach(child -> {
            if (child instanceof SvgGroup) {
                SvgGroup group = (SvgGroup) child;
                if (group.isVisible()) {
                    baseGroup.getChildren().add(group.createGraphic());
                }
            }
            if (child instanceof ISvgShape<?>) {
                ISvgShape<?> shape = (ISvgShape<?>) child;
                if (shape.isVisible()) {
                    baseGroup.getChildren().add(shape.createGraphic());
                }
            }
            if (child instanceof SvgText) {
                SvgText shape = (SvgText) child;
                baseGroup.getChildren().add(shape.createGraphic());
            }
        });
        return baseGroup;
    }

    public SvgGroup getBaseGroup() {
        if (content == null || content.isEmpty()) {
            return null;
        }
        return content.stream().filter(SvgGroup.class::isInstance).map(SvgGroup.class::cast).filter(SvgGroup::isVisible).findFirst().orElse(null);
    }

    public String getBaseProfile() {
        return baseProfile;
    }

    @Override
    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    public String getContentScriptType() {
        if (contentScriptType == null) {
            return "application/ecmascript";
        } else {
            return contentScriptType;
        }
    }

    public String getContentStyleType() {
        if (contentStyleType == null) {
            return "text/css";
        } else {
            return contentStyleType;
        }
    }

    public String getOnAbort() {
        return onAbort;
    }

    public String getOnError() {
        return onError;
    }

    public String getOnResize() {
        return onResize;
    }

    public String getOnScroll() {
        return onScroll;
    }

    public String getOnUnload() {
        return onUnload;
    }

    public String getOnZoom() {
        return onZoom;
    }

    public String getVersion() {
        if (version == null) {
            return "1.1";
        } else {
            return version;
        }
    }

    @XmlTransient
    public List<? extends ISvgElement> getVisibleContent() {
        return content.stream()
            .filter(AbstractSvgStylable.class::isInstance)
            .map(AbstractSvgStylable.class::cast)
            .filter(AbstractSvgStylable::isVisible)
            .collect(toList());
    }

    public String getZoomAndPan() {
        if (zoomAndPan == null) {
            return "magnify";
        } else {
            return zoomAndPan;
        }
    }

    public void setBaseProfile(String value) {
        this.baseProfile = value;
    }

    public void setBounds(Bounds bounds) {
        setX(new Size(bounds.getMinX(), SizeUnits.PX));
        setY(new Size(bounds.getMinY(), SizeUnits.PX));
        setWidth(new Size(bounds.getWidth(), SizeUnits.PX));
        setHeight(new Size(bounds.getHeight(), SizeUnits.PX));
    }

    public void setContentScriptType(String value) {
        this.contentScriptType = value;
    }

    public void setContentStyleType(String value) {
        this.contentStyleType = value;
    }

    public void setOnAbort(String value) {
        this.onAbort = value;
    }

    public void setOnError(String value) {
        this.onError = value;
    }

    public void setOnResize(String value) {
        this.onResize = value;
    }

    public void setOnScroll(String value) {
        this.onScroll = value;
    }

    public void setOnUnload(String value) {
        this.onUnload = value;
    }

    public void setOnZoom(String value) {
        this.onZoom = value;
    }

    public void setTitle(String title) {
        SvgTitle svgTitle = new SvgTitle();
        svgTitle.setValue(title);
        getContent().add(svgTitle);
    }

    public void setVersion(String value) {
        this.version = value;
    }

    public void setZoomAndPan(String value) {
        this.zoomAndPan = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        ISvgBounded.super.toStringDetail(builder);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgEventListener.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
        ISvgFitToViewBox.super.toStringDetail(builder);
    }

}
