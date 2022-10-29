package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.description.ISvgDescriptiveElement;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.helper.SvgExternalResources;

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


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "externalResources", "contents"
})
@XmlRootElement(name = "animate")
public class SvgAnimateAttribute extends AbstractSvgAnimationElement implements ISvgAttributeAnimation {

    @XmlPath(".")
    protected final SvgExternalResources externalResources = new SvgExternalResources();

    @XmlAttribute(name = "attributeName", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String attributeName;

    @XmlAttribute(name = "attributeType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String attributeType;

    @XmlAttribute(name = "calcMode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String calcMode;

    @XmlAttribute(name = "values")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String values;

    @XmlAttribute(name = "keyTimes")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String keyTimes;

    @XmlAttribute(name = "keySplines")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String keySplines;

    @XmlAttribute(name = "from")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String from;

    @XmlAttribute(name = "by")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String by;

    @XmlAttribute(name = "additive")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String additive;

    @XmlAttribute(name = "accumulate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String accumulate;

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

    public String getCalcMode() {
        if (calcMode == null) {
            return "linear";
        } else {
            return calcMode;
        }
    }

    public void setCalcMode(String value) {
        this.calcMode = value;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String value) {
        this.values = value;
    }

    public String getKeyTimes() {
        return keyTimes;
    }

    public void setKeyTimes(String value) {
        this.keyTimes = value;
    }

    public String getKeySplines() {
        return keySplines;
    }

    public void setKeySplines(String value) {
        this.keySplines = value;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String value) {
        this.from = value;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String value) {
        this.by = value;
    }

    public String getAdditive() {
        if (additive == null) {
            return "replace";
        } else {
            return additive;
        }
    }

    public void setAdditive(String value) {
        this.additive = value;
    }

    public String getAccumulate() {
        if (accumulate == null) {
            return "none";
        } else {
            return accumulate;
        }
    }

    public void setAccumulate(String value) {
        this.accumulate = value;
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

    @Override
    public SvgExternalResources getExternalResources() {
        return externalResources;
    }

}
