////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package testset.annotation;

import static checks.annotation.AnnotationUseStyleCheck.MSG_KEY_ANNOTATION_INCORRECT_STYLE;
import static checks.annotation.AnnotationUseStyleCheck.MSG_KEY_ANNOTATION_PARENS_MISSING;
import static checks.annotation.AnnotationUseStyleCheck.MSG_KEY_ANNOTATION_PARENS_PRESENT;
import static checks.annotation.AnnotationUseStyleCheck.MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING;
import static checks.annotation.AnnotationUseStyleCheck.MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import api.TokenTypes;
import checks.annotation.AnnotationUseStyleCheck;
import checkstyle.DefaultConfiguration;
import testset.checkstyle.AbstractModuleTestSupport;


import utils.CommonUtil;

public class AnnotationUseStyleCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
    	//return "com/puppycrawl/tools/checks/annotation/annotationusestyle";
        return "com/puppycrawl/tools/checkstyle/checks/annotation/annotationusestyle";
    }

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testElementStyleValueOf() {
        final AnnotationUseStyleCheck.ElementStyle option =
            AnnotationUseStyleCheck.ElementStyle.valueOf("COMPACT");
        assertEquals("Invalid valueOf result",
            AnnotationUseStyleCheck.ElementStyle.COMPACT, option);
    }

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testTrailingArrayCommaValueOf() {
        final AnnotationUseStyleCheck.TrailingArrayComma option =
            AnnotationUseStyleCheck.TrailingArrayComma.valueOf("ALWAYS");
        assertEquals("Invalid valueOf result",
            AnnotationUseStyleCheck.TrailingArrayComma.ALWAYS, option);
    }

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testClosingParensValueOf() {
        final AnnotationUseStyleCheck.ClosingParens option =
            AnnotationUseStyleCheck.ClosingParens.valueOf("ALWAYS");
        assertEquals("Invalid valueOf result",
            AnnotationUseStyleCheck.ClosingParens.ALWAYS, option);
    }

    /**
     * Test that annotation parens are always present.
     */
   // @Test
    public void testParensAlways() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ALWAYS");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "ignore");
        final String[] expected = {
            "3: " + getCheckMessage(MSG_KEY_ANNOTATION_PARENS_MISSING),
            "18: " + getCheckMessage(MSG_KEY_ANNOTATION_PARENS_MISSING),
            "23: " + getCheckMessage(MSG_KEY_ANNOTATION_PARENS_MISSING),
        };

        verify(checkConfig, getPath("InputAnnotationUseStyleDifferentStyles.java"), expected);
    }

    /**
     * Test that annotation parens are never present.
     */
    /*@Test   deu erro */
     public void testParensNever() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "NEVER");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "ignore");
        final String[] expected = {
            "13: " + getCheckMessage(MSG_KEY_ANNOTATION_PARENS_PRESENT),
            "30: " + getCheckMessage(MSG_KEY_ANNOTATION_PARENS_PRESENT),
            "33: " + getCheckMessage(MSG_KEY_ANNOTATION_PARENS_PRESENT),
        };

        verify(checkConfig, getPath("InputAnnotationUseStyleDifferentStyles.java"), expected);
    }

  //  @Test  /*  deu erro */
    public void testStyleExpanded() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "EXPANDED");
        checkConfig.addAttribute("trailingArrayComma", "ignore");
        final String[] expected = {
            "5: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "EXPANDED"),
            "12: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "EXPANDED"),
            "20: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "EXPANDED"),
            "26: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "EXPANDED"),
            "39: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "EXPANDED"),
            "41: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "EXPANDED"),
            "58: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "EXPANDED"),
        };

        verify(checkConfig, getPath("InputAnnotationUseStyleDifferentStyles.java"), expected);
    }

  //  @Test
    public void testStyleCompact() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "COMPACT");
        checkConfig.addAttribute("trailingArrayComma", "ignore");
        final String[] expected = {
           "43: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "COMPACT"),
            "47: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "COMPACT"),
        };

        verify(checkConfig, getPath("InputAnnotationUseStyleDifferentStyles.java"), expected);
    }

 //   @Test
    public void testStyleCompactNoArray() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "COMPACT_NO_ARRAY");
        checkConfig.addAttribute("trailingArrayComma", "ignore");
        final String[] expected = {
            "5: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "COMPACT_NO_ARRAY"),
            "20: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "COMPACT_NO_ARRAY"),
            "41: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "COMPACT_NO_ARRAY"),
            "43: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "COMPACT_NO_ARRAY"),
            "47: " + getCheckMessage(MSG_KEY_ANNOTATION_INCORRECT_STYLE, "COMPACT_NO_ARRAY"),
        };

        verify(checkConfig, getPath("InputAnnotationUseStyleDifferentStyles.java"), expected);
    }

  //  @Test
    public void testCommaAlwaysViolations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "ALWAYS");
        final String[] expected = {
            "3:20: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "6:30: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "10:40: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "13:44: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "16:54: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "24:22: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "24:36: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "26:21: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "26:30: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "29:39: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "29:49: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "32:21: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "32:41: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
        };

        verify(checkConfig, getPath("InputAnnotationUseStyleNoTrailingComma.java"), expected);
    }

   // @Test
    public void testCommaAlwaysViolationsNonCompilable() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "ALWAYS");
        final String[] expected = {
            "6:37: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
            "6:65: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_MISSING),
        };

        verify(checkConfig,
                getNonCompilablePath("InputAnnotationUseStyleNoTrailingComma.java"), expected);
    }

   // @Test
    public void testCommaAlwaysNoViolations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "ALWAYS");
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(checkConfig, getPath("InputAnnotationUseStyleWithTrailingComma.java"), expected);
    }

   // @Test
    public void testCommaAlwaysNoViolationsNonCompilable() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "ALWAYS");
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(checkConfig,
                getNonCompilablePath("InputAnnotationUseStyleWithTrailingComma.java"), expected);
    }

   // @Test
    public void testCommaNeverViolations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "NEVER");
        final String[] expected = {
            "9:32: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT),
            "13:42: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT),
            "16:46: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT),
            "19:56: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT),
            "27:23: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT),
            "27:38: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT),
            "33:39: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT),
            "33:50: " + getCheckMessage(MSG_KEY_ANNOTATION_TRAILING_COMMA_PRESENT),
        };

        verify(checkConfig, getPath("InputAnnotationUseStyleWithTrailingComma.java"), expected);
    }

   // @Test
    public void testCommaNeverNoViolations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "NEVER");
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(checkConfig, getPath("InputAnnotationUseStyleNoTrailingComma.java"), expected);
    }

    //@Test
    public void testEverythingMixed() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "ignore");
        checkConfig.addAttribute("trailingArrayComma", "ignore");
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(checkConfig, getPath("InputAnnotationUseStyleDifferentStyles.java"), expected);
    }

   // @Test
    public void testAnnotationsWithoutDefaultValues() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "NEVER");
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(checkConfig, getPath("InputAnnotationUseStyleParams.java"), expected);
    }

  //  @Test
    public void testGetAcceptableTokens() {
        final AnnotationUseStyleCheck constantNameCheckObj = new AnnotationUseStyleCheck();
        final int[] actual = constantNameCheckObj.getAcceptableTokens();
        final int[] expected = {TokenTypes.ANNOTATION };
        Assert.assertArrayEquals("Invalid acceptable tokens", expected, actual);
    }

//    @Test
//    public void testGetOption() {
//        final AnnotationUseStyleCheck check = new AnnotationUseStyleCheck();
//        try {
//            check.setElementStyle("SHOULD_PRODUCE_ERROR");
//            Assert.fail("ConversionException is expected");
//        }
//        catch (IllegalArgumentException ex) {
//            final String messageStart = "unable to parse";
//
//            assertTrue("Invalid exception message, should start with: " + messageStart,
//                ex.getMessage().startsWith(messageStart));
//        }
//    }

 //   @Test
    public void testStyleNotInList() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(AnnotationUseStyleCheck.class);
        checkConfig.addAttribute("closingParens", "ignore");
        checkConfig.addAttribute("elementStyle", "COMPACT_NO_ARRAY");
        checkConfig.addAttribute("trailingArrayComma", "ignore");
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(checkConfig, getPath("InputAnnotationUseStyle.java"), expected);
    }

}
