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

import java.util.Arrays;
import java.util.Locale;

import com.google.common.base.MoreObjects.ToStringHelper;

public interface ISvgConditionalFeatures extends ISvgAttributes {
    String COND_REQUIRED_FEATURES = "requiredFeatures";
    String COND_REQUIRED_EXTENSIONS = "requiredExtensions";
    String COND_SYSTEM_LANGUAGE = "systemLanguage";

    default String getRequiredExtensions() {
        return get(COND_REQUIRED_EXTENSIONS);
    }

    default String getRequiredFeatures() {
        return get(COND_REQUIRED_FEATURES);
    }

    default String getSystemLanguage() {
        return get(COND_SYSTEM_LANGUAGE);
    }

    /**
     * Whether {@code extension} appears in the whitespace-separated {@code requiredExtensions} list. Null-safe -
     * an absent attribute (the overwhelming majority of elements) previously threw {@link NullPointerException}
     * here, which nothing calling it today would have caught.
     */
    default boolean hasExtension(String extension) {
        String extensions = getRequiredExtensions();
        return extensions != null && Arrays.asList(extensions.trim().split("\\s+")).contains(extension);
    }

    /**
     * Whether {@code requiredFeatures} is satisfied: absent is true, present but blank is false, otherwise every
     * whitespace-separated feature string must be one this renderer declares in {@link SvgFeatures#SUPPORTED}.
     */
    default boolean requiredFeaturesSatisfied() {
        String features = getRequiredFeatures();
        if (features == null) {
            return true;
        }
        if (features.isBlank()) {
            return false;
        }
        for (String feature : features.trim().split("\\s+")) {
            if (!SvgFeatures.SUPPORTED.contains(feature)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether {@code requiredExtensions} is satisfied - the one attribute of the three where blank (rather than
     * absent) is explicitly true, meaning "no extension required." This renderer claims no extensions at all, so
     * any non-blank value is false.
     */
    default boolean requiredExtensionsSatisfied() {
        String extensions = getRequiredExtensions();
        return extensions == null || extensions.isBlank();
    }

    /**
     * Whether {@code systemLanguage} is satisfied against {@code locale}: absent is true, present but blank is
     * false, otherwise at least one comma-separated tag must match {@code locale}'s language tag exactly, or be a
     * prefix of it ending at a subtag boundary - {@code "en"} matches a locale of {@code en-NZ}.
     */
    default boolean systemLanguageSatisfied(Locale locale) {
        String systemLanguage = getSystemLanguage();
        if (systemLanguage == null) {
            return true;
        }
        if (systemLanguage.isBlank()) {
            return false;
        }
        String userTag = locale.toLanguageTag();
        for (String rawTag : systemLanguage.split(",")) {
            String tag = rawTag.trim();
            if (tag.isEmpty()) {
                continue;
            }
            if (userTag.equalsIgnoreCase(tag)
                || (userTag.length() > tag.length() && userTag.charAt(tag.length()) == '-' && userTag.regionMatches(true, 0, tag, 0, tag.length()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether this element's conditional processing attributes all pass, per {@code locale} - the single test
     * {@code <switch>} uses to pick its first passing child, and every other element implementing this interface
     * uses to decide whether it renders at all (see {@link ISvgContainer#isRendered}).
     */
    default boolean isConditionSatisfied(Locale locale) {
        return requiredFeaturesSatisfied() && requiredExtensionsSatisfied() && systemLanguageSatisfied(locale);
    }

    default void setRequiredExtensions(String value) {
        set(COND_REQUIRED_EXTENSIONS, value);
    }

    default void setRequiredFeatures(String value) {
        set(COND_REQUIRED_FEATURES, value);
    }

    default void setSystemLanguage(String value) {
        set(COND_SYSTEM_LANGUAGE, value);
    }

    default void toStringDetail(ToStringHelper builder) {
        builder.add(COND_REQUIRED_FEATURES, getRequiredFeatures());
        builder.add(COND_REQUIRED_EXTENSIONS, getRequiredExtensions());
        builder.add(COND_SYSTEM_LANGUAGE, getSystemLanguage());
    }

}
