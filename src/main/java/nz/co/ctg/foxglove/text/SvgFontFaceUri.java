package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgLinkable;
import nz.co.ctg.foxglove.attributes.SvgLinkableAttributes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "linkable", "fontFaceFormat"
})
@XmlRootElement(name = "font-face-uri")
public class SvgFontFaceUri extends AbstractSvgElement implements ISvgLinkable {

    @XmlElement(name = "font-face-format")
    private List<SvgFontFaceFormat> fontFaceFormat;

    @XmlPath(".")
    private final SvgLinkableAttributes linkable = new SvgLinkableAttributes();

    /**
     * Gets the value of the fontFaceFormat property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fontFaceFormat property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFontFaceFormat().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgFontFaceFormat }
     *
     *
     */
    public List<SvgFontFaceFormat> getFontFaceFormat() {
        if (fontFaceFormat == null) {
            fontFaceFormat = new ArrayList<>();
        }
        return this.fontFaceFormat;
    }

    @Override
    public SvgLinkableAttributes getLinkableAttributes() {
        return linkable;
    }

}
