package nz.co.ctg.foxglove.filter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feFuncB", propOrder = {
    "animations"
})
@XmlRootElement(name = "feFuncB")
public class FeFunctionBlue extends AbstractFeFunction implements ISvgFilterFunction  {
}
