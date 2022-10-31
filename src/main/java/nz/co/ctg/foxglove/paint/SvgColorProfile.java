package nz.co.ctg.foxglove.paint;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;
import nz.co.ctg.foxglove.description.ISvgDescriptiveElement;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "linkable", "content"
})
@XmlRootElement(name = "color-profile")
public class SvgColorProfile extends AbstractSvgElement implements ISvgElement, ISvgLinkable {

    @XmlAttribute(name = "local")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String local;

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String name;

    @XmlAttribute(name = "rendering-intent")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String renderingIntent;

    @XmlPath(".")
    private final SvgLinkableAttributes linkable = new SvgLinkableAttributes();

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgDescriptiveElement> content;

    public String getLocal() {
        return local;
    }

    public void setLocal(String value) {
        this.local = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getRenderingIntent() {
        if (renderingIntent == null) {
            return "auto";
        } else {
            return renderingIntent;
        }
    }

    public void setRenderingIntent(String value) {
        this.renderingIntent = value;
    }

    @Override
    public SvgLinkableAttributes getLinkableAttributes() {
        return linkable;
    }

    public List<ISvgDescriptiveElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("local", local);
        builder.add("name", name);
        builder.add("renderingIntent", renderingIntent);
        super.toStringDetail(builder);
        linkable.toStringDetail(builder);
    }

}
