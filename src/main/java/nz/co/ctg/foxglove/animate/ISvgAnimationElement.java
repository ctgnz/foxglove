package nz.co.ctg.foxglove.animate;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.ISvgAttributes;

public interface ISvgAnimationElement extends ISvgAttributes {
    String ANIM_ONBEGIN = "onbegin";
    String ANIM_ONEND = "onend";
    String ANIM_ONREPEAT = "onrepeat";
    String ANIM_ONLOAD = "onload";
    String ANIM_BEGIN = "begin";
    String ANIM_DUR = "dur";
    String ANIM_END = "end";
    String ANIM_MIN = "min";
    String ANIM_MAX = "max";
    String ANIM_RESTART = "restart";
    String ANIM_REPEAT_COUNT = "repeatCount";
    String ANIM_REPEAT_DUR = "repeatDur";
    String ANIM_FILL = "fill";
    String ANIM_TO = "to";

    default String getOnBegin() {
        return get(ANIM_ONBEGIN);
    }

    default void setOnBegin(String value) {
        set(ANIM_ONBEGIN, value);
    }

    default String getOnEnd() {
        return get(ANIM_ONEND);
    }

    default void setOnEnd(String value) {
        set(ANIM_ONEND, value);
    }

    default String getOnRepeat() {
        return get(ANIM_ONREPEAT);
    }

    default void setOnRepeat(String value) {
        set(ANIM_ONREPEAT, value);
    }

    default String getOnLoad() {
        return get(ANIM_ONLOAD);
    }

    default void setOnLoad(String value) {
        set(ANIM_ONLOAD, value);
    }

    default String getBegin() {
        return get(ANIM_BEGIN);
    }

    default void setBegin(String value) {
        set(ANIM_BEGIN, value);
    }

    default String getDuration() {
        return get(ANIM_DUR);
    }

    default void setDuration(String value) {
        set(ANIM_DUR, value);
    }

    default String getEnd() {
        return get(ANIM_END);
    }

    default void setEnd(String value) {
        set(ANIM_END, value);
    }

    default String getMin() {
        return get(ANIM_MIN);
    }

    default void setMin(String value) {
        set(ANIM_MIN, value);
    }

    default String getMax() {
        return get(ANIM_MAX);
    }

    default void setMax(String value) {
        set(ANIM_MAX, value);
    }

    default String getRestart() {
        return get(ANIM_RESTART);
    }

    default void setRestart(String value) {
        set(ANIM_RESTART, value);
    }

    default String getRepeatCount() {
        return get(ANIM_REPEAT_COUNT);
    }

    default void setRepeatCount(String value) {
        set(ANIM_REPEAT_COUNT, value);
    }

    default String getRepeatDuration() {
        return get(ANIM_REPEAT_DUR);
    }

    default void setRepeatDuration(String value) {
        set(ANIM_REPEAT_DUR, value);
    }

    default String getFill() {
        return get(ANIM_FILL);
    }

    default void setFill(String value) {
        set(ANIM_FILL, value);
    }

    default String getTo() {
        return get(ANIM_TO);
    }

    default void setTo(String value) {
        set(ANIM_TO, value);
    }

//    SVGElement getTargetElement();
//    float getStartTime();
//    float getCurrentTime();
//    float getSimpleDuration();

    default void toStringDetail(ToStringHelper builder) {
        builder.add(ANIM_ONBEGIN, getOnBegin());
        builder.add(ANIM_ONEND, getOnEnd());
        builder.add(ANIM_ONREPEAT, getOnRepeat());
        builder.add(ANIM_ONLOAD, getOnLoad());
        builder.add(ANIM_BEGIN, getBegin());
        builder.add(ANIM_DUR, getDuration());
        builder.add(ANIM_END, getEnd());
        builder.add(ANIM_MIN, getMin());
        builder.add(ANIM_MAX, getMax());
        builder.add(ANIM_RESTART, getRestart());
        builder.add(ANIM_REPEAT_COUNT, getRepeatCount());
        builder.add(ANIM_REPEAT_DUR, getRepeatDuration());
        builder.add(ANIM_FILL, getFill());
        builder.add(ANIM_TO, getTo());
    }

}
