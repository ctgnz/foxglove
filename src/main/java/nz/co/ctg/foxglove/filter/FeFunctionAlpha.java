package nz.co.ctg.foxglove.filter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feFuncA", propOrder = {
    "animations"
})
@XmlRootElement(name = "feFuncA")
public class FeFunctionAlpha extends AbstractFeFunction {
}
