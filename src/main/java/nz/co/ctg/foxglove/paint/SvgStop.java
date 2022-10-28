package nz.co.ctg.foxglove.paint;

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

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "stop")
public class SvgStop extends AbstractSvgStylable {

    @XmlAttribute(name = "offset", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String offset;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class),
        @XmlElement(name = "set", type = SvgSetAttribute.class),
        @XmlElement(name = "animateColor", type = SvgAnimateColor.class)
    })
    protected List<Object> content;

    /**
     * Gets the value of the offset property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOffset(String value) {
        this.offset = value;
    }

    /**
     * Gets the value of the content property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgAnimateAttribute }
     * {@link SvgSetAttribute }
     * {@link SvgAnimateColor }
     *
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        // TODO Auto-generated method stub

    }

}
