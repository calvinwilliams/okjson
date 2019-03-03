package xyz.calvinwilliams.okjson;

import java.util.*;

import xyz.calvinwilliams.okjson.TestDataClass;

/**
 * @author calvin
 *
 */
public class TestObjectToJsonString {
	
	public static void setTestDataObject( TestDataClass object ) {
		
		Branch7		branch7 ;
		
		object.str1 = "str1" ;
		object.byte1 = 1 ;
		object.short1 = 12 ;
		object.int1 = 1234 ;
		object.long1 = 12345678L ;
		object.float1 = 1.2f ;
		object.double1 = 12.34 ;
		object.boolean1 = true ;
		object.name1 = null ;
		
		object.branch2 = new Branch2() ;
		object.branch2.str2 = "str2" ;
		object.branch2.byte2 = 2 ;
		object.branch2.short2 = 23 ;
		object.branch2.int2 = 2345 ;
		object.branch2.long2 = 23456789L ;
		object.branch2.float2 = 2.3f ;
		object.branch2.double2 = 23.45 ;
		object.branch2.boolean2 = false ;
		
		object.array4 = new LinkedList<String>() ;
		object.array4.add( "AAA" );
		object.array4.add( "BBB" );
		object.array4.add( "CCC" );
		
		object.array5 = new ArrayList<Integer>() ;
		object.array5.add( 12 );
		object.array5.add( 34 );
		object.array5.add( 56 );
		
		object.array6 = new LinkedList<Boolean>() ;
		object.array6.add( true );
		object.array6.add( false );
		object.array6.add( true );
		
		object.array7 = new ArrayList<Branch7>() ;
		
		branch7 = new Branch7() ;
		branch7.str7 = "str71" ;
		branch7.int7 = 71 ;
		object.array7.add(branch7);
		
		branch7 = new Branch7() ;
		branch7.str7 = "str72" ;
		branch7.int7 = 0 ;
		object.array7.add(branch7);
		
		branch7 = new Branch7() ;
		branch7.str7 = null ;
		branch7.int7 = 0 ;
		object.array7.add(branch7);
		
		object.strings8 = new Strings8() ;
		object.strings8.setString81( "\"\\/\b\f\n\r\t" );
		object.strings8.setString82( "ABC\"\\/\b\f\n\r\t" );
		object.strings8.setString83( "\"\\/\b\f\n\r\tABC" );
		object.strings8.setString84( "A\"\\/B\b\f\n\r\tC" );
		object.strings8.setString85( "\u8BA1\u7B97\u673A\u5B66\u9662" );
		object.strings8.setString86( "ABC\u8BA1\u7B97\u673A\u5B66\u9662" );
		object.strings8.setString87( "\u8BA1\u7B97\u673A\u5B66\u9662ABC" );
		object.strings8.setString88( "A\u8BA1\u7B97B\u673A\u5B66\u9662C" );
		object.strings8.setString89( "\u8BA1A\u7B97\u673AB\u5B66C\u9662" );
		
		object.numbers9 = new Numbers9() ;
		object.numbers9.setLong91( 123 );
		object.numbers9.setLong92( -123 );
		object.numbers9.setLong93( 1234567890 );
		object.numbers9.setFloatE94( 12.34E2f );
		object.numbers9.setFloatE95( -12.34E-2f );
		object.numbers9.setFloatE96( -0.1234E4f );
		object.numbers9.setDoubleE97( 1234.5678 );
		object.numbers9.setDoubleE98( 1234.5678E2 );
		object.numbers9.setDoubleE99( 1234.5678E-2 );
		
		return;
	}
	
	public static void testObjectToJsonString() {
		
		TestDataClass		object = new TestDataClass() ;
		
		setTestDataObject( object ) ;
		
		OkJson okjson = new OkJson() ;
		okjson.setDirectAccessPropertyEnable( true );
		okjson.setPrettyFormatEnable(true);
		String jsonString = okjson.objectToString( object ) ;
		if( jsonString == null ) {
			System.out.println( "objectToString failed["+okjson.getErrorCode()+"]["+okjson.getErrorDesc()+"]" );
			return;
		} else {
			System.out.println( "objectToString ok" );
			System.out.println( "jsonString["+jsonString+"]" );
		}
		
		return;
	}
	
	public static void main(String[] args) {
		testObjectToJsonString();
		return;
	}
}
