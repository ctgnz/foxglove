package nz.co.ctg.foxglove.filter;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feFuncG", propOrder = {
    "function"
})
@XmlRootElement(name = "feFuncG")
public class FilterEffectFunctionGreen extends AbstractSvgElement implements ISvgFilterFunction  {

    private final FilterEffectCompositeFunction function = new FilterEffectCompositeFunction();

    @Override
    public FilterEffectCompositeFunction getFunctionAttributes() {
        return function;
    }

    @Override
    protected void toStringDetail(ToStringHelper builder) {
        function.toStringDetail(builder);
        super.toStringDetail(builder);
    }

}
