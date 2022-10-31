package nz.co.ctg.foxglove.attributes;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgEventListener;

import static com.google.common.base.MoreObjects.toStringHelper;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class SvgEventListener {
    @XmlAttribute(name = "onfocusin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onFocusIn;

    @XmlAttribute(name = "onfocusout")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onFocusOut;

    @XmlAttribute(name = "onactivate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onActivate;

    @XmlAttribute(name = "onclick")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onClick;

    @XmlAttribute(name = "onmousedown")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseDown;

    @XmlAttribute(name = "onmouseup")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseUp;

    @XmlAttribute(name = "onmouseover")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseOver;

    @XmlAttribute(name = "onmousemove")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseMove;

    @XmlAttribute(name = "onmouseout")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onMouseOut;

    @XmlAttribute(name = "onload")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onLoad;

    public String getOnFocusIn() {
        return onFocusIn;
    }

    public void setOnFocusIn(String value) {
        this.onFocusIn = value;
    }

    public String getOnFocusOut() {
        return onFocusOut;
    }

    public void setOnFocusOut(String value) {
        this.onFocusOut = value;
    }

    public String getOnActivate() {
        return onActivate;
    }

    public void setOnActivate(String value) {
        this.onActivate = value;
    }

    public String getOnClick() {
        return onClick;
    }

    public void setOnClick(String value) {
        this.onClick = value;
    }

    public String getOnMouseDown() {
        return onMouseDown;
    }

    public void setOnMouseDown(String value) {
        this.onMouseDown = value;
    }

    public String getOnMouseUp() {
        return onMouseUp;
    }

    public void setOnMouseUp(String value) {
        this.onMouseUp = value;
    }

    public String getOnMouseOver() {
        return onMouseOver;
    }

    public void setOnMouseOver(String value) {
        this.onMouseOver = value;
    }

    public String getOnMouseMove() {
        return onMouseMove;
    }

    public void setOnMouseMove(String value) {
        this.onMouseMove = value;
    }

    public String getOnMouseOut() {
        return onMouseOut;
    }

    public void setOnMouseOut(String value) {
        this.onMouseOut = value;
    }

    public String getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(String value) {
        this.onLoad = value;
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper(ISvgEventListener.class.getSimpleName()).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add("onfocusin", getOnFocusIn());
        builder.add("onfocusout", getOnFocusOut());
        builder.add("onactivate", getOnActivate());
        builder.add("onclick", getOnClick());
        builder.add("onmousedown", getOnMouseDown());
        builder.add("onmouseup", getOnMouseUp());
        builder.add("onmouseover", getOnMouseOver());
        builder.add("onmousemove", getOnMouseMove());
        builder.add("onmouseout", getOnMouseOut());
    }
}
