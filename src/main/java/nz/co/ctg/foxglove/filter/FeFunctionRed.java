package nz.co.ctg.foxglove.filter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feFuncR", propOrder = {
    "animations"
})
@XmlRootElement(name = "feFuncR")
public class FeFunctionRed extends AbstractFeFunction implements ISvgFilterFunction {

}
