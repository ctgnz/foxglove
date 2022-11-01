package nz.co.ctg.foxglove.filter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feFuncA", propOrder = {
    "function"
})
@XmlRootElement(name = "feFuncA")
public class FilterEffectFunctionAlpha extends AbstractSvgElement implements ISvgFilterFunction  {

    private final CompositeFilterEffectFunction function = new CompositeFilterEffectFunction();

    @Override
    public CompositeFilterEffectFunction getFunctionAttributes() {
        return function;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        function.toStringDetail(builder);
        super.toStringDetail(builder);
    }

}