/*
 * Copyright (c) 2010 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package nz.co.ctg.foxglove;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

import com.google.common.base.MoreObjects.ToStringHelper;

public interface ISvgStylable extends ISvgAttributes, ISvgPresentationAttributes, ISvgGraphicsAttributes, ISvgTextAttributes {
    String STYLE_ATTR = "style";
    String STYLE_CLASS_NAME = "class";

    default CSSStyleDeclaration getStyleDeclaration() {
        return null;
    }

    default CSSValue getPresentationAttribute(String name) {
        return null;
    }

    default String getClassName() {
        return get(STYLE_CLASS_NAME);
    }

    default void setClassName(String className) {
        set(STYLE_CLASS_NAME, className);
    }

    default String getStyle() {
        return get(STYLE_ATTR);
    }

    default void setStyle(String style) {
        set(STYLE_ATTR, style);
    }

    @Override
    default void toStringDetail(ToStringHelper builder) {
        builder.add(STYLE_ATTR, getStyle());
        builder.add(STYLE_CLASS_NAME, getClassName());
        ISvgGraphicsAttributes.super.toStringDetail(builder);
        ISvgPresentationAttributes.super.toStringDetail(builder);
        ISvgTextAttributes.super.toStringDetail(builder);
    }
}
