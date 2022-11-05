package nz.co.ctg.foxglove.animate;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;
import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;

import jakarta.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class AbstractSvgAnimationElement extends AbstractSvgElement implements ISvgAnimationElement, ISvgConditionalFeatures, ISvgLinkable, ISvgExternalResources {

    @Override
    public void toStringDetail(ToStringHelper builder) {
        ISvgAnimationElement.super.toStringDetail(builder);
        super.toStringDetail(builder);
        ISvgConditionalFeatures.super.toStringDetail(builder);
        ISvgLinkable.super.toStringDetail(builder);
        ISvgExternalResources.super.toStringDetail(builder);
    }

}
