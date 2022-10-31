package nz.co.ctg.foxglove.attributes;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgLinkable;

import static com.google.common.base.MoreObjects.toStringHelper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class SvgLinkableAttributes {
    private static final String ATTR_ACTUATE = "xlink:actuate";
    private static final String ATTR_SHOW = "xlink:show";
    private static final String ATTR_TITLE = "xlink:title";
    private static final String ATTR_ARCROLE = "xlink:arcrole";
    private static final String ATTR_ROLE = "xlink:role";
    private static final String ATTR_HREF = "xlink:href";
    private static final String ATTR_TYPE = "xlink:type";
    private static final String ATTR_XMLNS_XLINK = "xmlns:xlink";

    @XmlAttribute(name = ATTR_XMLNS_XLINK)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xmlnsXlink;

    @XmlAttribute(name = ATTR_TYPE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String xlinkType;

    @XmlAttribute(name = ATTR_HREF, required = true, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkHref;

    @XmlAttribute(name = ATTR_ROLE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkRole;

    @XmlAttribute(name = ATTR_ARCROLE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkArcrole;

    @XmlAttribute(name = ATTR_TITLE, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String xlinkTitle;

    @XmlAttribute(name = ATTR_SHOW, namespace = "http://www.w3.org/1999/xlink")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String xlinkShow;

    @XmlAttribute(name = ATTR_ACTUATE, namespace = "http://www.w3.org/1999/xlink")
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

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper(ISvgLinkable.class.getSimpleName()).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add(ATTR_XMLNS_XLINK, xmlnsXlink);
        builder.add(ATTR_TYPE, xlinkType);
        builder.add(ATTR_HREF, xlinkHref);
        builder.add(ATTR_ROLE, xlinkRole);
        builder.add(ATTR_ARCROLE, xlinkArcrole);
        builder.add(ATTR_TITLE, xlinkTitle);
        builder.add(ATTR_SHOW, xlinkShow);
        builder.add(ATTR_ACTUATE, xlinkActuate);
    }

}
