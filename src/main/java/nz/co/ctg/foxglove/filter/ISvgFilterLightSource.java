package nz.co.ctg.foxglove.filter;

import java.util.List;

import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.animate.ISvgAnimationElement;

public interface ISvgFilterLightSource extends ISvgElement {

    List<ISvgAnimationElement> getAnimations();

}
