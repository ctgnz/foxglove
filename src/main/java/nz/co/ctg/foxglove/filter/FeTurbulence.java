package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;
import nz.co.ctg.foxglove.animate.ISvgAnimationElement;
import nz.co.ctg.foxglove.animate.SvgAnimateAttribute;
import nz.co.ctg.foxglove.animate.SvgSetAttribute;

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


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "animations"
})
@XmlRootElement(name = "feTurbulence")
public class FeTurbulence extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlAttribute(name = "baseFrequency")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String baseFrequency;

    @XmlAttribute(name = "numOctaves")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String numOctaves;

    @XmlAttribute(name = "seed")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String seed;

    @XmlAttribute(name = "stitchTiles")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String stitchTiles;

    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String type;

    @XmlElements({
        @XmlElement(name = "animate", type = SvgAnimateAttribute.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "set", type = SvgSetAttribute.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgAnimationElement> animations;

    public String getBaseFrequency() {
        return baseFrequency;
    }

    public void setBaseFrequency(String value) {
        this.baseFrequency = value;
    }

    public String getNumOctaves() {
        return numOctaves;
    }

    public void setNumOctaves(String value) {
        this.numOctaves = value;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String value) {
        this.seed = value;
    }

    public String getStitchTiles() {
        if (stitchTiles == null) {
            return "noStitch";
        } else {
            return stitchTiles;
        }
    }

    public void setStitchTiles(String value) {
        this.stitchTiles = value;
    }

    public String getType() {
        if (type == null) {
            return "turbulence";
        } else {
            return type;
        }
    }

    public void setType(String value) {
        this.type = value;
    }

    public List<ISvgAnimationElement> getAnimations() {
        if (animations == null) {
            animations = new ArrayList<>();
        }
        return this.animations;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("baseFrequency", baseFrequency);
        builder.add("numOctaves", numOctaves);
        builder.add("seed", seed);
        builder.add("stitchTiles", stitchTiles);
        builder.add("type", type);
        super.toStringDetail(builder);
        ISvgFilterPrimitive.super.toStringDetail(builder);
    }

}
