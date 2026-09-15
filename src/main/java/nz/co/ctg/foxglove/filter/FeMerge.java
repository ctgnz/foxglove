package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "feMergeNode"
})
@XmlRootElement(name = "feMerge")
public class FeMerge extends AbstractSvgStylable implements ISvgFilterPrimitive {

    // the namespace is not optional: this package's elementFormDefault is UNQUALIFIED, so a binding that omits it
    // matches an element in no namespace at all, never the SVG-namespaced children a real document actually carries
    @XmlElement(name = "feMergeNode", namespace = "http://www.w3.org/2000/svg")
    private List<FeMergeNode> feMergeNode;

    public List<FeMergeNode> getFeMergeNode() {
        if (feMergeNode == null) {
            feMergeNode = new ArrayList<>();
        }
        return this.feMergeNode;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("feMergeNode", feMergeNode);
        super.toStringDetail(builder);
        ISvgFilterPrimitive.super.toStringDetail(builder);
    }

}
