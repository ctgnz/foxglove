package nz.co.ctg.foxglove.animate;

import com.google.common.base.MoreObjects.ToStringHelper;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class SvgAnimationAttributes {

    @XmlAttribute(name = "onbegin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String onBegin;

    @XmlAttribute(name = "onend")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String onEnd;

    @XmlAttribute(name = "onrepeat")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String onRepeat;

    @XmlAttribute(name = "onload")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String onLoad;

    @XmlAttribute(name = "begin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String begin;

    @XmlAttribute(name = "dur")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String duration;

    @XmlAttribute(name = "end")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String end;

    @XmlAttribute(name = "min")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String min;

    @XmlAttribute(name = "max")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String max;

    @XmlAttribute(name = "restart")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String restart;

    @XmlAttribute(name = "repeatCount")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String repeatCount;

    @XmlAttribute(name = "repeatDur")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String repeatDuration;

    @XmlAttribute(name = "fill")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String fill;

    @XmlAttribute(name = "to")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String to;

    public String getOnBegin() {
        return onBegin;
    }

    public void setOnBegin(String value) {
        this.onBegin = value;
    }

    public String getOnEnd() {
        return onEnd;
    }

    public void setOnEnd(String value) {
        this.onEnd = value;
    }

    public String getOnRepeat() {
        return onRepeat;
    }

    public void setOnRepeat(String value) {
        this.onRepeat = value;
    }

    public String getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(String value) {
        this.onLoad = value;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String value) {
        this.begin = value;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String value) {
        this.duration = value;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String value) {
        this.end = value;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String value) {
        this.min = value;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String value) {
        this.max = value;
    }

    public String getRestart() {
        if (restart == null) {
            return "always";
        } else {
            return restart;
        }
    }

    public void setRestart(String value) {
        this.restart = value;
    }

    public String getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(String value) {
        this.repeatCount = value;
    }

    public String getRepeatDuration() {
        return repeatDuration;
    }

    public void setRepeatDuration(String value) {
        this.repeatDuration = value;
    }

    public String getFill() {
        if (fill == null) {
            return "remove";
        } else {
            return fill;
        }
    }

    public void setFill(String value) {
        this.fill = value;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String value) {
        this.to = value;
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add("onBegin", onBegin);
        builder.add("onEnd", onEnd);
        builder.add("onRepeat", onRepeat);
        builder.add("onLoad", onLoad);
        builder.add("begin", begin);
        builder.add("duration", duration);
        builder.add("end", end);
        builder.add("min", min);
        builder.add("max", max);
        builder.add("restart", restart);
        builder.add("repeatCount", repeatCount);
        builder.add("repeatDuration", repeatDuration);
        builder.add("fill", fill);
        builder.add("to", to);
    }

}
