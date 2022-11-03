package nz.co.ctg.foxglove;

import org.eclipse.persistence.oxm.annotations.XmlValueExtension;

import com.google.common.base.MoreObjects.ToStringHelper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "style")
public class SvgStyle extends AbstractSvgElement implements ISvgElement {

    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String type;

    @XmlAttribute(name = "media")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String media;

    @XmlAttribute(name = "title")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String title;

    @XmlValueExtension
    protected String value;

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String value) {
        this.media = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("type", type);
        builder.add("media", media);
        builder.add("title", title);
        super.toStringDetail(builder);
        builder.add("value", value);
    }
}
