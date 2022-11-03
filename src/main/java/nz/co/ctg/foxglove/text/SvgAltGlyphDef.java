package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "glyphItems"
})
@XmlRootElement(name = "altGlyphDef")
public class SvgAltGlyphDef extends AbstractSvgElement implements ISvgElement {

    @XmlElements({
        @XmlElement(name = "glyphRef", required = true, type = SvgGlyphRef.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "altGlyphItem", required = true, type = SvgAltGlyphItem.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgGlyphItem> glyphItems;

    public List<ISvgGlyphItem> getGlyphItems() {
        if (glyphItems == null) {
            glyphItems = new ArrayList<>();
        }
        return this.glyphItems;
    }

}
