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
		
		object.list4 = new LinkedList<String>() ;
		object.list4.add( "AAA" );
		object.list4.add( "BBB" );
		object.list4.add( "CCC" );
		
		object.list5 = new ArrayList<Integer>() ;
		object.list5.add( 12 );
		object.list5.add( 34 );
		object.list5.add( 56 );
		
		object.list6 = new LinkedList<Boolean>() ;
		object.list6.add( true );
		object.list6.add( false );
		object.list6.add( true );
		
		object.list7 = new ArrayList<Branch7>() ;
		
		branch7 = new Branch7() ;
		branch7.str7 = "str71" ;
		branch7.int7 = 71 ;
		object.list7.add(branch7);
		
		branch7 = new Branch7() ;
		branch7.str7 = "str72" ;
		branch7.int7 = 0 ;
		object.list7.add(branch7);
		
		branch7 = new Branch7() ;
		branch7.str7 = null ;
		branch7.int7 = 0 ;
		object.list7.add(branch7);
		
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
