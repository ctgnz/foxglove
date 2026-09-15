package nz.co.ctg.foxglove.conformance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Covers pulling a test's own pass criteria out of its source document (#110) - the part of a test page that says what was actually being checked, which no pixel ratio conveys.
 */
public class ConformanceTestDescriptionTest {

    @Test
    public void testPassCriteriaAreFlattenedToPlainText() {
        String criteria = ConformanceTestDescription.passCriteriaOf("""
                        <svg xmlns="http://www.w3.org/2000/svg">
                          <d:SVGTestCase xmlns:d="http://www.w3.org/2000/02/svg/testsuite/description/">
                            <d:passCriteria xmlns="http://www.w3.org/1999/xhtml">
                              <p>
                                The rendered picture should match the reference image,
                                except for variations in the labelling text.
                              </p>
                            </d:passCriteria>
                          </d:SVGTestCase>
                        </svg>
                        """);

        assertThat(criteria,
            is("The rendered picture should match the reference image, except for variations in the labelling text."));
    }

    @Test
    public void testMultipleParagraphsAreJoinedIntoOnePassage() {
        String criteria = ConformanceTestDescription.passCriteriaOf(
            "<d:passCriteria><p>First point.</p><p>Second point.</p></d:passCriteria>");

        assertThat(criteria, is("First point. Second point."));
    }

    @Test
    public void testADocumentWithNoPassCriteriaYieldsAnEmptyString() {
        assertThat(ConformanceTestDescription.passCriteriaOf("<svg xmlns=\"http://www.w3.org/2000/svg\"/>"), is(""));
    }

}
