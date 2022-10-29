package nz.co.ctg.foxglove.animate;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.helper.SvgConditionalFeatures;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public abstract class AbstractSvgAnimationElement extends AbstractSvgElement implements ISvgAnimationElement {

    @XmlPath(".")
    protected final SvgConditionalFeatures conditionalFeatures = new SvgConditionalFeatures();

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

    @XmlAttribute(name = "xmlns:xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlnsXlink;

    @XmlAttribute(name = "xlink:type", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkType;

    @XmlAttribute(name = "xlink:href", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkHref;

    @XmlAttribute(name = "xlink:role", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkRole;

    @XmlAttribute(name = "xlink:arcrole", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkArcrole;

    @XmlAttribute(name = "xlink:title", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkTitle;

    @XmlAttribute(name = "xlink:show", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkShow;

    @XmlAttribute(name = "xlink:actuate", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkActuate;

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
    public SvgConditionalFeatures getConditionalFeatures() {
        return conditionalFeatures;
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

    @Override
    public String getXmlnsXlink() {
        if (xmlnsXlink == null) {
            return "http://www.w3.org/1999/xlink";
        } else {
            return xmlnsXlink;
        }
    }

    @Override
    public void setXmlnsXlink(String value) {
        this.xmlnsXlink = value;
    }

    @Override
    public String getXlinkType() {
        if (xlinkType == null) {
            return "simple";
        } else {
            return xlinkType;
        }
    }

    @Override
    public void setXlinkType(String value) {
        this.xlinkType = value;
    }

    @Override
    public String getXlinkHref() {
        return xlinkHref;
    }

    @Override
    public void setXlinkHref(String value) {
        this.xlinkHref = value;
    }

    @Override
    public String getXlinkRole() {
        return xlinkRole;
    }

    @Override
    public void setXlinkRole(String value) {
        this.xlinkRole = value;
    }

    @Override
    public String getXlinkArcrole() {
        return xlinkArcrole;
    }

    @Override
    public void setXlinkArcrole(String value) {
        this.xlinkArcrole = value;
    }

    @Override
    public String getXlinkTitle() {
        return xlinkTitle;
    }

    @Override
    public void setXlinkTitle(String value) {
        this.xlinkTitle = value;
    }

    @Override
    public String getXlinkShow() {
        if (xlinkShow == null) {
            return "other";
        } else {
            return xlinkShow;
        }
    }

    @Override
    public void setXlinkShow(String value) {
        this.xlinkShow = value;
    }

    @Override
    public String getXlinkActuate() {
        if (xlinkActuate == null) {
            return "onLoad";
        } else {
            return xlinkActuate;
        }
    }

    @Override
    public void setXlinkActuate(String value) {
        this.xlinkActuate = value;
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
