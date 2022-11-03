package nz.co.ctg.foxglove.text;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgLinkable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fontFaceFormat"
})
@XmlRootElement(name = "font-face-uri")
public class SvgFontFaceUri extends AbstractSvgElement implements ISvgElement, ISvgLinkable {

    @XmlElement(name = "font-face-format")
    private List<SvgFontFaceFormat> fontFaceFormat;

    public List<SvgFontFaceFormat> getFontFaceFormat() {
        if (fontFaceFormat == null) {
            fontFaceFormat = new ArrayList<>();
        }
        return this.fontFaceFormat;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
    }

}
