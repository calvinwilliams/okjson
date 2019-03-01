package xyz.calvinwilliams.okjson;

import java.util.*;

import xyz.calvinwilliams.okjson.TestDataClass;

/**
 * @author calvin
 *
 */
public class TestObjectToJsonString {
	
	public static void setTestDataObject( TestDataClass object ) {
		
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
		
		/*
		System.out.println( "------------------------------ dump TestDataClass" );
		System.out.println( "TestDataClass.str1["+object.str1+"]" );
		System.out.println( "TestDataClass.byte1["+object.byte1+"]" );
		System.out.println( "TestDataClass.short1["+object.short1+"]" );
		System.out.println( "TestDataClass.int1["+object.int1+"]" );
		System.out.println( "TestDataClass.long1["+object.long1+"]" );
		System.out.println( "TestDataClass.float1["+object.float1+"]" );
		System.out.println( "TestDataClass.double1["+object.double1+"]" );
		System.out.println( "TestDataClass.boolean1["+object.boolean1+"]" );
		System.out.println( "TestDataClass.name1["+object.name1+"]" );
		
		System.out.println( "------------------------------ dump TestDataClass.branch2" );
		if( object.branch2 != null ) {
			System.out.println( "TestDataClass.branch2.str2["+object.branch2.str2+"]" );
			System.out.println( "TestDataClass.branch2.byte2["+object.branch2.byte2+"]" );
			System.out.println( "TestDataClass.branch2.short2["+object.branch2.short2+"]" );
			System.out.println( "TestDataClass.branch2.int2["+object.branch2.int2+"]" );
			System.out.println( "TestDataClass.branch2.long2["+object.branch2.long2+"]" );
			System.out.println( "TestDataClass.branch2.float2["+object.branch2.float2+"]" );
			System.out.println( "TestDataClass.branch2.double2["+object.branch2.double2+"]" );
			System.out.println( "TestDataClass.branch2.boolean2["+object.branch2.boolean2+"]" );
		}
		
		System.out.println( "------------------------------ dump TestDataClass.branch3" );
		if( object.branch3 != null ) {
			System.out.println( "TestDataClass.branch3.str3["+object.branch3.str3+"]" );
			System.out.println( "TestDataClass.branch3.byte3["+object.branch3.byte3+"]" );
			System.out.println( "TestDataClass.branch3.short3["+object.branch3.short3+"]" );
			System.out.println( "TestDataClass.branch3.int3["+object.branch3.int3+"]" );
			System.out.println( "TestDataClass.branch3.Long3["+object.branch3.long3+"]" );
			System.out.println( "TestDataClass.branch3.Float3["+object.branch3.float3+"]" );
			System.out.println( "TestDataClass.branch3.double3["+object.branch3.double3+"]" );
			System.out.println( "TestDataClass.branch3.boolean3["+object.branch3.boolean3+"]" );
			
			System.out.println( "------------------------------ dump TestDataClass.branch3.branch33" );
			if( object.branch3.branch33 != null ) {
				System.out.println( "TestDataClass.branch3.branch33.str33["+object.branch3.branch33.str33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.byte33["+object.branch3.branch33.byte33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.short33["+object.branch3.branch33.short33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.Int33["+object.branch3.branch33.int33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.long33["+object.branch3.branch33.long33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.float33["+object.branch3.branch33.float33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.Double33["+object.branch3.branch33.double33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.Boolean33["+object.branch3.branch33.boolean33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.name33["+object.branch3.branch33.name33+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.list4" );
		if( object.list4 != null ) {
			Iterator<String> iterList4 = object.list4.iterator() ;
			while( iterList4.hasNext() ) {
				String value = iterList4.next() ;
				System.out.println( "TestDataClass.list4.<String>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.list5" );
		if( object.list5 != null ) {
			Iterator<Integer> iterList5 = object.list5.iterator() ;
			while( iterList5.hasNext() ) {
				Integer value = iterList5.next() ;
				System.out.println( "TestDataClass.list5.<Integer>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.list6" );
		if( object.list6 != null ) {
			Iterator<Boolean> iterList6 = object.list6.iterator() ;
			while( iterList6.hasNext() ) {
				Boolean value = iterList6.next() ;
				System.out.println( "TestDataClass.list6.<Boolean>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.list7" );
		if( object.list7 != null ) {
			Iterator<Branch7> iterList7 = object.list7.iterator() ;
			while( iterList7.hasNext() ) {
				Branch7 value = iterList7.next() ;
				System.out.println( "TestDataClass.list6.<Branch7>.str7["+value.str7+"] .int7["+value.int7+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump end" );
		*/
	}
	
	public static void testObjectToJsonString() {
		
		TestDataClass		object = new TestDataClass() ;
		
		setTestDataObject( object ) ;
		
		OkJson okjson = new OkJson() ;
		okjson.setDirectAccessPropertyEnable( true );
		okjson.setFormatCompactEnable(true);
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
