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
    "content"
})
@XmlRootElement(name = "font-face-src")
public class SvgFontFaceSrc extends AbstractSvgElement implements ISvgElement {

    @XmlElements({
        @XmlElement(name = "font-face-uri", required = true, type = SvgFontFaceUri.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "font-face-name", required = true, type = SvgFontFaceName.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgElement> content;

    public List<ISvgElement> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

}
