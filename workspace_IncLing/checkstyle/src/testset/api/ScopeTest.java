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

package testset.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import api.Scope;

/**
 * Test cases for {@link Scope} enumeration.
 */
public class ScopeTest {

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testScopeValueOf() {
        final Scope scope = Scope.valueOf("PRIVATE");
        assertEquals("Invalid scope", Scope.PRIVATE, scope);
    }

//    @Test
//    public void testMisc() {
//        final Scope scope = Scope.getInstance("public");
//        assertNotNull("Scope must not be null", scope);
//        assertEquals("Invalid scope toString", "public", scope.toString());
//        assertEquals("Invalid scope name", "public", scope.getName());
//
//        try {
//            Scope.getInstance("unknown");
//            fail("exception expected");
//        }
//        catch (IllegalArgumentException ex) {
//            assertEquals("Invalid error message",
//                    "No enum constant com.puppycrawl.tools.checkstyle.api.Scope.UNKNOWN",
//                    ex.getMessage());
//        }
//    }

    @Test
    public void testMixedCaseSpaces() {
        assertEquals("Invalid scope", Scope.NOTHING, Scope.getInstance("NothinG "));
        assertEquals("Invalid scope", Scope.PUBLIC, Scope.getInstance(" PuBlic"));
        assertEquals("Invalid scope", Scope.PROTECTED, Scope.getInstance(" ProteCted"));
        assertEquals("Invalid scope", Scope.PACKAGE, Scope.getInstance("    PackAge "));
        assertEquals("Invalid scope", Scope.PRIVATE, Scope.getInstance("privaTe   "));
        assertEquals("Invalid scope", Scope.ANONINNER, Scope.getInstance("AnonInner"));
    }

//    @Test
//    public void testMixedCaseSpacesWithDifferentLocales() {
//        final Locale[] differentLocales = {new Locale("TR", "tr") };
//        final Locale defaultLocale = Locale.getDefault();
//        try {
//            for (Locale differentLocale : differentLocales) {
//                Locale.setDefault(differentLocale);
//                assertEquals("Invalid scope", Scope.NOTHING, Scope.getInstance("NothinG "));
//                assertEquals("Invalid scope", Scope.PUBLIC, Scope.getInstance(" PuBlic"));
//                assertEquals("Invalid scope", Scope.PROTECTED, Scope.getInstance(" ProteCted"));
//                assertEquals("Invalid scope", Scope.PACKAGE, Scope.getInstance("    PackAge "));
//                assertEquals("Invalid scope", Scope.PRIVATE, Scope.getInstance("privaTe   "));
//                assertEquals("Invalid scope", Scope.ANONINNER, Scope.getInstance("AnonInner"));
//            }
//        }
//        finally {
//            Locale.setDefault(defaultLocale);
//        }
//    }

    @Test
    public void testIsInAnonInner() {
        assertTrue("Invalid subscope", Scope.NOTHING.isIn(Scope.ANONINNER));
        assertTrue("Invalid subscope", Scope.PUBLIC.isIn(Scope.ANONINNER));
        assertTrue("Invalid subscope", Scope.PROTECTED.isIn(Scope.ANONINNER));
        assertTrue("Invalid subscope", Scope.PACKAGE.isIn(Scope.ANONINNER));
        assertTrue("Invalid subscope", Scope.PRIVATE.isIn(Scope.ANONINNER));
        assertTrue("Invalid subscope", Scope.ANONINNER.isIn(Scope.ANONINNER));
    }

    @Test
    public void testIsInPrivate() {
        assertTrue("Invalid subscope", Scope.NOTHING.isIn(Scope.PRIVATE));
        assertTrue("Invalid subscope", Scope.PUBLIC.isIn(Scope.PRIVATE));
        assertTrue("Invalid subscope", Scope.PROTECTED.isIn(Scope.PRIVATE));
        assertTrue("Invalid subscope", Scope.PACKAGE.isIn(Scope.PRIVATE));
        assertTrue("Invalid subscope", Scope.PRIVATE.isIn(Scope.PRIVATE));
        assertFalse("Invalid subscope", Scope.ANONINNER.isIn(Scope.PRIVATE));
    }

    @Test
    public void testIsInPackage() {
        assertTrue("Invalid subscope", Scope.NOTHING.isIn(Scope.PACKAGE));
        assertTrue("Invalid subscope", Scope.PUBLIC.isIn(Scope.PACKAGE));
        assertTrue("Invalid subscope", Scope.PROTECTED.isIn(Scope.PACKAGE));
        assertTrue("Invalid subscope", Scope.PACKAGE.isIn(Scope.PACKAGE));
        assertFalse("Invalid subscope", Scope.PRIVATE.isIn(Scope.PACKAGE));
        assertFalse("Invalid subscope", Scope.ANONINNER.isIn(Scope.PACKAGE));
    }

    @Test
    public void testIsInProtected() {
        assertTrue("Invalid subscope", Scope.NOTHING.isIn(Scope.PROTECTED));
        assertTrue("Invalid subscope", Scope.PUBLIC.isIn(Scope.PROTECTED));
        assertTrue("Invalid subscope", Scope.PROTECTED.isIn(Scope.PROTECTED));
        assertFalse("Invalid subscope", Scope.PACKAGE.isIn(Scope.PROTECTED));
        assertFalse("Invalid subscope", Scope.PRIVATE.isIn(Scope.PROTECTED));
        assertFalse("Invalid subscope", Scope.ANONINNER.isIn(Scope.PROTECTED));
    }

    @Test
    public void testIsInPublic() {
        assertTrue("Invalid subscope", Scope.NOTHING.isIn(Scope.PUBLIC));
        assertTrue("Invalid subscope", Scope.PUBLIC.isIn(Scope.PUBLIC));
        assertFalse("Invalid subscope", Scope.PROTECTED.isIn(Scope.PUBLIC));
        assertFalse("Invalid subscope", Scope.PACKAGE.isIn(Scope.PUBLIC));
        assertFalse("Invalid subscope", Scope.PRIVATE.isIn(Scope.PUBLIC));
        assertFalse("Invalid subscope", Scope.ANONINNER.isIn(Scope.PUBLIC));
    }

    @Test
    public void testIsInNothing() {
        assertTrue("Invalid subscope", Scope.NOTHING.isIn(Scope.NOTHING));
        assertFalse("Invalid subscope", Scope.PUBLIC.isIn(Scope.NOTHING));
        assertFalse("Invalid subscope", Scope.PROTECTED.isIn(Scope.NOTHING));
        assertFalse("Invalid subscope", Scope.PACKAGE.isIn(Scope.NOTHING));
        assertFalse("Invalid subscope", Scope.PRIVATE.isIn(Scope.NOTHING));
        assertFalse("Invalid subscope", Scope.ANONINNER.isIn(Scope.NOTHING));
    }

}
