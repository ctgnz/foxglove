package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import nz.co.ctg.foxglove.AbstractSvgElement;

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
    "glyphRef"
})
@XmlRootElement(name = "altGlyphItem")
public class SvgAltGlyphItem extends AbstractSvgElement implements ISvgGlyphItem {

    @XmlElement(required = true)
    private List<SvgGlyphRef> glyphRef;

    /**
     * Gets the value of the glyphRef property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the glyphRef property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlyphRef().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgGlyphRef }
     *
     *
     */
    public List<SvgGlyphRef> getGlyphRef() {
        if (glyphRef == null) {
            glyphRef = new ArrayList<>();
        }
        return this.glyphRef;
    }

}
