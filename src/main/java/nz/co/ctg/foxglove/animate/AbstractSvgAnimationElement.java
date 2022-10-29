package nz.co.ctg.foxglove.animate;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.attributes.SvgConditionalFeaturesAttributes;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public abstract class AbstractSvgAnimationElement extends AbstractSvgElement implements ISvgAnimationElement {

    @XmlPath(".")
    protected final SvgConditionalFeaturesAttributes conditionalFeatures = new SvgConditionalFeaturesAttributes();

    @XmlPath(".")
    protected final SvgLinkableAttributes linkable = new SvgLinkableAttributes();

    @XmlAttribute(name = "onbegin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onbegin;

    @XmlAttribute(name = "onend")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onend;

    @XmlAttribute(name = "onrepeat")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onrepeat;

    @XmlAttribute(name = "onload")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onload;

    @XmlAttribute(name = "begin")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String begin;

    @XmlAttribute(name = "dur")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String dur;

    @XmlAttribute(name = "end")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String end;

    @XmlAttribute(name = "min")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String min;

    @XmlAttribute(name = "max")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String max;

    @XmlAttribute(name = "restart")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String restart;

    @XmlAttribute(name = "repeatCount")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String repeatCount;

    @XmlAttribute(name = "repeatDur")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String repeatDur;

    @XmlAttribute(name = "fill")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fill;

    @XmlAttribute(name = "to")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String to;

    @Override
    public SvgConditionalFeaturesAttributes getConditionalFeaturesAttributes() {
        return conditionalFeatures;
    }

    @Override
    public SvgLinkableAttributes getLinkableAttributes() {
        return linkable;
    }

    public String getOnbegin() {
        return onbegin;
    }

    public void setOnbegin(String value) {
        this.onbegin = value;
    }

    public String getOnend() {
        return onend;
    }

    public void setOnend(String value) {
        this.onend = value;
    }

    public String getOnrepeat() {
        return onrepeat;
    }

    public void setOnrepeat(String value) {
        this.onrepeat = value;
    }

    public String getOnload() {
        return onload;
    }

    public void setOnload(String value) {
        this.onload = value;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String value) {
        this.begin = value;
    }

    public String getDur() {
        return dur;
    }

    public void setDur(String value) {
        this.dur = value;
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

    public String getRepeatDur() {
        return repeatDur;
    }

    public void setRepeatDur(String value) {
        this.repeatDur = value;
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

}
