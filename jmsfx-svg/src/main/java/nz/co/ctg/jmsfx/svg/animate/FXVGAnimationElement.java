package nz.co.ctg.jmsfx.svg.animate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nz.co.ctg.jmsfx.svg.AbstractFXVGElement;
import nz.co.ctg.jmsfx.svg.FXVGConditionalFeatures;
import nz.co.ctg.jmsfx.svg.FXVGExternalResources;

public abstract class FXVGAnimationElement extends AbstractFXVGElement implements FXVGExternalResources, FXVGConditionalFeatures {

    @XmlAttribute(name = "requiredFeatures")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredFeatures;

    @XmlAttribute(name = "requiredExtensions")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String requiredExtensions;

    @XmlAttribute(name = "systemLanguage")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String systemLanguage;

    @XmlAttribute(name = "externalResourcesRequired")
    protected boolean externalResourcesRequired;

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

    @XmlAttribute(name = "xlink:type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkType;

    @XmlAttribute(name = "xlink:href")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkHref;

    @XmlAttribute(name = "xlink:role")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkRole;

    @XmlAttribute(name = "xlink:arcrole")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkArcrole;

    @XmlAttribute(name = "xlink:title")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xlinkTitle;

    @XmlAttribute(name = "xlink:show")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xlinkShow;

    @XmlAttribute(name = "xlink:actuate")
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
    public String getRequiredFeatures() {
        return requiredFeatures;
    }

    @Override
    public void setRequiredFeatures(String value) {
        this.requiredFeatures = value;
    }

    @Override
    public String getRequiredExtensions() {
        return requiredExtensions;
    }

    @Override
    public void setRequiredExtensions(String value) {
        this.requiredExtensions = value;
    }

    @Override
    public String getSystemLanguage() {
        return systemLanguage;
    }

    @Override
    public void setSystemLanguage(String value) {
        this.systemLanguage = value;
    }

    @Override
    public boolean getExternalResourcesRequired() {
        return externalResourcesRequired;
    }

    @Override
    public void setExternalResourcesRequired(boolean value) {
        this.externalResourcesRequired = value;
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

    public String getXmlnsXlink() {
        if (xmlnsXlink == null) {
            return "http://www.w3.org/1999/xlink";
        } else {
            return xmlnsXlink;
        }
    }

    public void setXmlnsXlink(String value) {
        this.xmlnsXlink = value;
    }

    public String getXlinkType() {
        if (xlinkType == null) {
            return "simple";
        } else {
            return xlinkType;
        }
    }

    public void setXlinkType(String value) {
        this.xlinkType = value;
    }

    public String getXlinkHref() {
        return xlinkHref;
    }

    public void setXlinkHref(String value) {
        this.xlinkHref = value;
    }

    public String getXlinkRole() {
        return xlinkRole;
    }

    public void setXlinkRole(String value) {
        this.xlinkRole = value;
    }

    public String getXlinkArcrole() {
        return xlinkArcrole;
    }

    public void setXlinkArcrole(String value) {
        this.xlinkArcrole = value;
    }

    public String getXlinkTitle() {
        return xlinkTitle;
    }

    public void setXlinkTitle(String value) {
        this.xlinkTitle = value;
    }

    public String getXlinkShow() {
        if (xlinkShow == null) {
            return "other";
        } else {
            return xlinkShow;
        }
    }

    public void setXlinkShow(String value) {
        this.xlinkShow = value;
    }

    public String getXlinkActuate() {
        if (xlinkActuate == null) {
            return "onLoad";
        } else {
            return xlinkActuate;
        }
    }

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
