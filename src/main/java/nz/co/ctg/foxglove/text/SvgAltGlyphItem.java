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

    // The namespace is not optional: elementFormDefault is UNQUALIFIED, so a binding without one matches a
    // glyphRef in *no* namespace and silently never populates against a real SVG document (#105, #119).
    @XmlElement(name = "glyphRef", required = true, namespace = "http://www.w3.org/2000/svg")
    private List<SvgGlyphRef> glyphRef;

    public List<SvgGlyphRef> getGlyphRef() {
        if (glyphRef == null) {
            glyphRef = new ArrayList<>();
        }
        return this.glyphRef;
    }

}
