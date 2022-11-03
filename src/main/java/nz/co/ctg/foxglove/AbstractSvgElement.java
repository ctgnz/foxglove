package nz.co.ctg.foxglove;

import com.google.common.base.MoreObjects.ToStringHelper;

import static com.google.common.base.MoreObjects.toStringHelper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractSvgElement implements ISvgElement {

    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;

    @XmlAttribute(name = "xml:base")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlBase;

    @XmlAttribute(name = "xml:lang")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xmlLang;

    @XmlAttribute(name = "xml:space")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xmlSpace;

    public AbstractSvgElement() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String value) {
        this.id = value;
    }

    @Override
    public String getXmlBase() {
        return xmlBase;
    }

    @Override
    public void setXmlBase(String value) {
        this.xmlBase = value;
    }

    @Override
    public String getXmlLang() {
        return xmlLang;
    }

    @Override
    public void setXmlLang(String value) {
        this.xmlLang = value;
    }

    @Override
    public String getXmlSpace() {
        return xmlSpace;
    }

    @Override
    public void setXmlSpace(String value) {
        this.xmlSpace = value;
    }

    @Override
    public String toString() {
        ToStringHelper builder = toStringHelper(getElementName()).omitNullValues();
        toStringDetail(builder);
        return builder.toString();
    }

    protected void toStringDetail(ToStringHelper builder) {
        builder.add("id", id);
        builder.add("xmlBase", xmlBase);
        builder.add("xmlLang", xmlLang);
        builder.add("xmlSpace", xmlSpace);
    }

}