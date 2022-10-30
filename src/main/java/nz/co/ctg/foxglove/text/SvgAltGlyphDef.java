package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import nz.co.ctg.foxglove.AbstractSvgElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "glyphItems"
})
@XmlRootElement(name = "altGlyphDef")
public class SvgAltGlyphDef extends AbstractSvgElement {

    @XmlElements({
        @XmlElement(name = "glyphRef", required = true, type = SvgGlyphRef.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "altGlyphItem", required = true, type = SvgAltGlyphItem.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgGlyphItem> glyphItems;

    /**
     * Gets the value of the glyphRefOrAltGlyphItem property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the glyphRefOrAltGlyphItem property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlyphRefOrAltGlyphItem().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SvgGlyphRef }
     * {@link SvgAltGlyphItem }
     *
     *
     */
    public List<ISvgGlyphItem> getGlyphItems() {
        if (glyphItems == null) {
            glyphItems = new ArrayList<>();
        }
        return this.glyphItems;
    }

}
