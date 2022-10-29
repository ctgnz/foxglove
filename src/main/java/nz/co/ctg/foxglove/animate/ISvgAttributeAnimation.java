package nz.co.ctg.foxglove.animate;

public interface ISvgAttributeAnimation extends ISvgAnimationElement {

    void setAttributeType(String value);

    String getAttributeType();

    void setAttributeName(String value);

    String getAttributeName();

}
