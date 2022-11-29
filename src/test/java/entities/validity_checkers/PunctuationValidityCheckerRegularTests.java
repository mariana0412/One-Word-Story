package entities.validity_checkers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PunctuationValidityCheckerRegularTests {

    public final PunctuationValidityChecker pvc =
            new PunctuationValidityCheckerRegular();

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Tests scenario where blank punctuation is checked
     */
    @Test(timeout = 1000)
    public void testNoPunctuation() {
        String punc = "";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals("", validifiedPunc);
    }

    /**
     * Tests scenario where entirely invalid punctuation is checked
     */
    @Test(timeout = 1000)
    public void testInvalidPunctuation() {
        String punc = "not_right";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertNull(validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with a single, non-duplicable character is checked
     */
    @Test(timeout = 1000)
    public void testNonDuplicableSingle() {
        String punc = ";";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals(";", validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with a single dash (character needed to be escaped)
     * is checked
     */
    @Test(timeout = 1000)
    public void testNonDuplicableSingleDash() {
        String punc = "-";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals("-", validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with a single, duplicable period is checked
     */
    @Test(timeout = 1000)
    public void testDuplicablePeriodSingle() {
        String punc = ".";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals(".", validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with a single, duplicable mark is checked
     */
    @Test(timeout = 1000)
    public void testDuplicableMarkSingle() {
        String punc = "?";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals("?", validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with multiple, non-duplicable characters is checked
     */
    @Test(timeout = 1000)
    public void testNonDuplicableMultiple() {
        String punc = ";,: equinox";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertNull(validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with multiple, duplicable periods is checked
     */
    @Test(timeout = 1000)
    public void testDuplicablePeriodMultiple() {
        String punc = "...";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals("...", validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with multiple, duplicable marks is checked
     */
    @Test(timeout = 1000)
    public void testDuplicableMarkMultiple() {
        String punc = "?!!";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals("?!!", validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with non-duplicable characters combined with duplicable
     * ones is checked
     */
    @Test(timeout = 1000)
    public void testSingleNonDuplicableAndDuplicable() {
        String punc = ",..";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertNull(validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with more than three valid characters is checked
     */
    @Test(timeout = 1000)
    public void testMoreThanThreeValidCharacters() {
        String punc = "??????????????";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals("???", validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with more than three characters
     * (the first three only are valid) is checked
     */
    @Test(timeout = 1000)
    public void testFirstThreeValidCharacters() {
        String punc = "...kkkkkk";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertEquals("...", validifiedPunc);
    }

    /**
     * Tests scenario where punctuation with more than three invalid characters is checked
     */
    @Test(timeout = 1000)
    public void testMoreThanThreeInvalidCharacters() {
        String punc = "AAAAAAAAA";
        String validifiedPunc = pvc.isPunctuationValid(punc);
        assertNull(validifiedPunc);
    }
}
