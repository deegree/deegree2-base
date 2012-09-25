package org.deegree.framework.util;

import junit.framework.TestCase;

public class StringToolsTest extends TestCase {

    private static String NULLSTRING = null;

    private static String EMPTY_STRING = "";

    private static String TEXT_STRING = "text";
    private static String TEXT_STRING1 = "text1";
    private static String TEXT_STRING2 = "text2";
    private static String TEXT_STRING3 = "text3";

    public void test_concat_one_string() {
        //arrange
        String expected = TEXT_STRING;
        //act
        String actual = StringTools.concat( 1, TEXT_STRING );
        //assert
        assertEquals( expected, actual );
    }
    
    public void test_concat_with_empty_String() {
        //arrange
        String expected = TEXT_STRING;
        //act
        String actual = StringTools.concat( 2, TEXT_STRING, EMPTY_STRING );
        //assert
        assertEquals( expected, actual );
    }
    
    public void test_concat_with_null_String() {
        //arrange
        String expected = TEXT_STRING;
        //act
        String actual = StringTools.concat( 2, TEXT_STRING, NULLSTRING );
        //assert
        assertEquals( expected, actual );
    }
    
    public void test_concat_two_Strings() {
        //arrange
        String expected = TEXT_STRING1.concat( TEXT_STRING2 );
        //act
        String actual = StringTools.concat( 2, TEXT_STRING1, TEXT_STRING2 );
        //assert
        assertEquals( expected, actual );
    }
    
    public void test_concat_three_Strings() {
        //arrange
        String expected = TEXT_STRING1.concat( TEXT_STRING2 ).concat( TEXT_STRING3 );
        //act
        String actual = StringTools.concat( 3, TEXT_STRING1, TEXT_STRING2, TEXT_STRING3 );
        //assert
        assertEquals( expected, actual );
    }
}
