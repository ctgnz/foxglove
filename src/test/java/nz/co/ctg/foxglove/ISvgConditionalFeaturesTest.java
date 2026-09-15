package nz.co.ctg.foxglove;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import nz.co.ctg.foxglove.shape.SvgRectangle;

/**
 * Exercises #22's evaluation rules: an absent conditional attribute is true, one present but blank is false except {@code requiredExtensions} (blank is explicitly true there),
 * {@code hasExtension} no longer throws on an absent attribute, and {@code systemLanguage="en"} matches an {@code en-NZ} locale.
 */
public class ISvgConditionalFeaturesTest {

    // --- hasExtension --------------------------------------------------------

    @Test
    public void testHasExtensionDoesNotThrowWhenAbsent() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        assertThat(rect.hasExtension("http://example.com/ext"), is(false));
    }

    @Test
    public void testHasExtensionTokenizesRatherThanSubstringMatching() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setRequiredExtensions("ext1 ext12");
        assertThat(rect.hasExtension("ext1"), is(true));
        // a naive contains() check would wrongly match "ext1" against "ext12" too
        assertThat(rect.hasExtension("ext2"), is(false));
    }

    // --- requiredFeatures ------------------------------------------------------

    @Test
    public void testAbsentRequiredFeaturesIsSatisfied() throws Exception {
        assertThat(new SvgRectangle().requiredFeaturesSatisfied(), is(true));
    }

    @Test
    public void testBlankRequiredFeaturesIsNotSatisfied() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setRequiredFeatures("");
        assertThat(rect.requiredFeaturesSatisfied(), is(false));
    }

    @Test
    public void testSupportedRequiredFeatureIsSatisfied() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setRequiredFeatures("http://www.w3.org/TR/SVG11/feature#Shape");
        assertThat(rect.requiredFeaturesSatisfied(), is(true));
    }

    @Test
    public void testUnsupportedRequiredFeatureIsNotSatisfied() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setRequiredFeatures("http://www.w3.org/TR/SVG11/feature#Animation");
        assertThat(rect.requiredFeaturesSatisfied(), is(false));
    }

    @Test
    public void testAllRequiredFeaturesMustBeSupported() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setRequiredFeatures("http://www.w3.org/TR/SVG11/feature#Shape http://www.w3.org/TR/SVG11/feature#Animation");
        assertThat(rect.requiredFeaturesSatisfied(), is(false));
    }

    // --- requiredExtensions ----------------------------------------------------

    @Test
    public void testAbsentRequiredExtensionsIsSatisfied() throws Exception {
        assertThat(new SvgRectangle().requiredExtensionsSatisfied(), is(true));
    }

    @Test
    public void testBlankRequiredExtensionsIsSatisfied() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setRequiredExtensions("");
        assertThat(rect.requiredExtensionsSatisfied(), is(true));
    }

    @Test
    public void testAnyRequiredExtensionIsNotSatisfied() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setRequiredExtensions("http://example.com/ext");
        assertThat(rect.requiredExtensionsSatisfied(), is(false));
    }

    // --- systemLanguage ----------------------------------------------------

    @Test
    public void testAbsentSystemLanguageIsSatisfied() throws Exception {
        assertThat(new SvgRectangle().systemLanguageSatisfied(Locale.forLanguageTag("en-NZ")), is(true));
    }

    @Test
    public void testBlankSystemLanguageIsNotSatisfied() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setSystemLanguage("");
        assertThat(rect.systemLanguageSatisfied(Locale.forLanguageTag("en-NZ")), is(false));
    }

    @Test
    public void testShortTagMatchesAMoreSpecificLocale() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setSystemLanguage("en");
        assertThat(rect.systemLanguageSatisfied(Locale.forLanguageTag("en-NZ")), is(true));
    }

    @Test
    public void testExactTagMatches() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setSystemLanguage("fr");
        assertThat(rect.systemLanguageSatisfied(Locale.forLanguageTag("fr")), is(true));
    }

    @Test
    public void testUnrelatedLanguageDoesNotMatch() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setSystemLanguage("fr");
        assertThat(rect.systemLanguageSatisfied(Locale.forLanguageTag("en-NZ")), is(false));
    }

    @Test
    public void testTagIsNotJustASubstringPrefix() throws Exception {
        // "eng" must not be treated as a loose prefix of "en-NZ"
        SvgRectangle rect = new SvgRectangle();
        rect.setSystemLanguage("eng");
        assertThat(rect.systemLanguageSatisfied(Locale.forLanguageTag("en-NZ")), is(false));
    }

    @Test
    public void testOneOfACommaSeparatedListCanMatch() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setSystemLanguage("fr, en");
        assertThat(rect.systemLanguageSatisfied(Locale.forLanguageTag("en-NZ")), is(true));
    }

    // --- combined ------------------------------------------------------------

    @Test
    public void testIsConditionSatisfiedRequiresAllThree() throws Exception {
        SvgRectangle rect = new SvgRectangle();
        rect.setRequiredFeatures("http://www.w3.org/TR/SVG11/feature#Shape");
        rect.setSystemLanguage("fr");
        assertThat(rect.isConditionSatisfied(Locale.forLanguageTag("en-NZ")), is(false));
    }

    @Test
    public void testIsConditionSatisfiedWhenAllAbsent() throws Exception {
        assertThat(new SvgRectangle().isConditionSatisfied(Locale.forLanguageTag("en-NZ")), is(true));
    }

}
