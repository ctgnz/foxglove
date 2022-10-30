package nz.co.ctg.foxglove.attributes;

import com.google.common.base.MoreObjects.ToStringHelper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgLinkableAttributes {
    @XmlAttribute(name = "xmlns:xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xmlnsXlink;

    @XmlAttribute(name = "xlink:type", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String xlinkType;

    @XmlAttribute(name = "xlink:href", required = true, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkHref;

    @XmlAttribute(name = "xlink:role", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkRole;

    @XmlAttribute(name = "xlink:arcrole", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkArcrole;

    @XmlAttribute(name = "xlink:title", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkTitle;

    @XmlAttribute(name = "xlink:show", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String xlinkShow;

    @XmlAttribute(name = "xlink:actuate", namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String xlinkActuate;

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

    public void toStringDetail(ToStringHelper builder) {
        builder.add("xmlnsXlink", xmlnsXlink);
        builder.add("xlinkType", xlinkType);
        builder.add("xlinkHref", xlinkHref);
        builder.add("xlinkRole", xlinkRole);
        builder.add("xlinkArcrole", xlinkArcrole);
        builder.add("xlinkTitle", xlinkTitle);
        builder.add("xlinkShow", xlinkShow);
        builder.add("xlinkActuate", xlinkActuate);
    }

}
