package nz.co.ctg.foxglove.animate;

import nz.co.ctg.foxglove.ISvgConditionalFeatures;
import nz.co.ctg.foxglove.ISvgElement;
import nz.co.ctg.foxglove.ISvgExternalResources;
import nz.co.ctg.foxglove.ISvgLinkable;

public interface ISvgAnimationElement extends ISvgElement, ISvgExternalResources, ISvgConditionalFeatures, ISvgLinkable {

    default void setTo(String value) {
        getAnimationAttributes().setTo(value);
    }

    default String getTo() {
        return getAnimationAttributes().getTo();
    }

    default void setFill(String value) {
        getAnimationAttributes().setFill(value);
    }

    default String getFill() {
        return getAnimationAttributes().getFill();
    }

    default void setRepeatDuration(String value) {
        getAnimationAttributes().setRepeatDuration(value);
    }

    default String getRepeatDuration() {
        return getAnimationAttributes().getRepeatDuration();
    }

    default void setRepeatCount(String value) {
        getAnimationAttributes().setRepeatCount(value);
    }

    default String getRepeatCount() {
        return getAnimationAttributes().getRepeatCount();
    }

    default void setRestart(String value) {
        getAnimationAttributes().setRestart(value);
    }

    default String getRestart() {
        return getAnimationAttributes().getRestart();
    }

    default void setMax(String value) {
        getAnimationAttributes().setMax(value);
    }

    default String getMax() {
        return getAnimationAttributes().getMax();
    }

    default void setMin(String value) {
        getAnimationAttributes().setMin(value);
    }

    default String getMin() {
        return getAnimationAttributes().getMin();
    }

    default void setEnd(String value) {
        getAnimationAttributes().setEnd(value);
    }

    default String getEnd() {
        return getAnimationAttributes().getEnd();
    }

    default void setDuration(String value) {
        getAnimationAttributes().setDuration(value);
    }

    default String getDuration() {
        return getAnimationAttributes().getDuration();
    }

    default void setBegin(String value) {
        getAnimationAttributes().setBegin(value);
    }

    default String getBegin() {
        return getAnimationAttributes().getBegin();
    }

    default void setOnLoad(String value) {
        getAnimationAttributes().setOnLoad(value);
    }

    default String getOnLoad() {
        return getAnimationAttributes().getOnLoad();
    }

    default void setOnRepeat(String value) {
        getAnimationAttributes().setOnRepeat(value);
    }

    default String getOnRepeat() {
        return getAnimationAttributes().getOnRepeat();
    }

    default void setOnEnd(String value) {
        getAnimationAttributes().setOnEnd(value);
    }

    default String getOnEnd() {
        return getAnimationAttributes().getEnd();
    }

    default void setOnBegin(String value) {
        getAnimationAttributes().setOnBegin(value);
    }

    default String getOnBegin() {
        return getAnimationAttributes().getOnBegin();
    }

    SvgAnimationAttributes getAnimationAttributes();

//    SVGElement getTargetElement();
//    float getStartTime();
//    float getCurrentTime();
//    float getSimpleDuration();

}
