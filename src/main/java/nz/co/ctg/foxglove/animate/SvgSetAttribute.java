package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.description.ISvgDescriptiveElement;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contents"
})
@XmlRootElement(name = "set")
public class SvgSetAttribute extends AbstractSvgAnimationElement implements ISvgAttributeAnimation {

    @XmlAttribute(name = "attributeName", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String attributeName;

    @XmlAttribute(name = "attributeType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String attributeType;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    protected List<ISvgDescriptiveElement> contents;

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    @Override
    public String getAttributeType() {
        return attributeType;
    }

    @Override
    public void setAttributeType(String value) {
        this.attributeType = value;
    }

    /**
     * Gets the value of the descOrTitleOrMetadata property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descOrTitleOrMetadata property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescOrTitleOrMetadata().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgDescription }
     * {@link SvgTitle }
     * {@link SvgMetadata }
     *
     *
     */
    public List<ISvgDescriptiveElement> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return this.contents;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        // TODO Auto-generated method stub

    }

}
