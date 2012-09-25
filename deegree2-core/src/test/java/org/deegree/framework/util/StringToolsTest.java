//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.framework.util;

import junit.framework.TestCase;

/**
 * 
 * test class for {@link StringTools}
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author$
 * 
 * @version 1.0. $Revision$, $Date$
 * 
 * @since 2.0
 */
public class StringToolsTest extends TestCase {

    private static int INITIAL_BUFFER_SIZE = 20;

    private static String NULLSTRING = null;

    private static String EMPTY_STRING = "";

    private static String TEXT_STRING = "text";

    private static String TEXT_STRING1 = "text1";

    private static String TEXT_STRING2 = "text2";

    private static String TEXT_STRING3 = "text3";

    public void test_concat_one_string() {
        // arrange
        String expected = TEXT_STRING;
        // act
        String actual = StringTools.concat( INITIAL_BUFFER_SIZE, TEXT_STRING );
        // assert
        assertEquals( expected, actual );
    }

    public void test_concat_with_empty_String() {
        // arrange
        String expected = TEXT_STRING;
        // act
        String actual = StringTools.concat( INITIAL_BUFFER_SIZE, TEXT_STRING, EMPTY_STRING );
        // assert
        assertEquals( expected, actual );
    }

    public void test_concat_with_null_String() {
        // arrange
        String expected = TEXT_STRING;
        // act
        String actual = StringTools.concat( INITIAL_BUFFER_SIZE, TEXT_STRING, NULLSTRING );
        // assert
        assertEquals( expected, actual );
    }

    public void test_concat_two_Strings() {
        // arrange
        String expected = TEXT_STRING1.concat( TEXT_STRING2 );
        // act
        String actual = StringTools.concat( INITIAL_BUFFER_SIZE, TEXT_STRING1, TEXT_STRING2 );
        // assert
        assertEquals( expected, actual );
    }

    public void test_concat_three_Strings() {
        // arrange
        String expected = TEXT_STRING1.concat( TEXT_STRING2 ).concat( TEXT_STRING3 );
        // act
        String actual = StringTools.concat( INITIAL_BUFFER_SIZE, TEXT_STRING1, TEXT_STRING2, TEXT_STRING3 );
        // assert
        assertEquals( expected, actual );
    }
}
