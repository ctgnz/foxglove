package nz.co.ctg.foxglove.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgStylable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "filter", "feMergeNode"
})
@XmlRootElement(name = "feMerge")
public class FeMerge extends AbstractSvgStylable implements ISvgFilterPrimitive {

    @XmlPath(".")
    private final SvgFilterAttributes filter = new SvgFilterAttributes();

    private List<FeMergeNode> feMergeNode;

    @Override
    public SvgFilterAttributes getFilterAttributes() {
        return filter;
    }

    public List<FeMergeNode> getFeMergeNode() {
        if (feMergeNode == null) {
            feMergeNode = new ArrayList<>();
        }
        return this.feMergeNode;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        builder.add("feMergeNode", feMergeNode);
        super.toStringDetail(builder);
        filter.toStringDetail(builder);
    }

}
