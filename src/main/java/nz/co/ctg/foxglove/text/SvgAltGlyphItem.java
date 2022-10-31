package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import nz.co.ctg.foxglove.AbstractSvgElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "glyphRef"
})
@XmlRootElement(name = "altGlyphItem")
public class SvgAltGlyphItem extends AbstractSvgElement implements ISvgGlyphItem {

    @XmlElement(required = true)
    private List<SvgGlyphRef> glyphRef;

    public List<SvgGlyphRef> getGlyphRef() {
        if (glyphRef == null) {
            glyphRef = new ArrayList<>();
        }
        return this.glyphRef;
    }

}
