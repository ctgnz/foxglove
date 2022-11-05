package nz.co.ctg.foxglove.filter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feFuncG", propOrder = {
    "animations"
})
@XmlRootElement(name = "feFuncG")
public class FeFunctionGreen extends AbstractFeFunction implements ISvgFilterFunction  {
}