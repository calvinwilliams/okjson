/*
 * okjson - A easy JSON parser/generator for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.okjson;

import java.util.*;

/**
 * @author calvin
 *
 */
public class TestObjectToJsonStringToObject {
	
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
	
	public static void printTestDataClass( TestDataClass obj ) {
		System.out.println( "------------------------------ dump TestDataClass" );
		System.out.println( "TestDataClass.str1["+obj.str1+"]" );
		System.out.println( "TestDataClass.byte1["+obj.byte1+"]" );
		System.out.println( "TestDataClass.short1["+obj.short1+"]" );
		System.out.println( "TestDataClass.int1["+obj.int1+"]" );
		System.out.println( "TestDataClass.long1["+obj.long1+"]" );
		System.out.println( "TestDataClass.float1["+obj.float1+"]" );
		System.out.println( "TestDataClass.double1["+obj.double1+"]" );
		System.out.println( "TestDataClass.boolean1["+obj.boolean1+"]" );
		System.out.println( "TestDataClass.name1["+obj.name1+"]" );
		
		System.out.println( "------------------------------ dump TestDataClass.branch2" );
		if( obj.branch2 != null ) {
			System.out.println( "TestDataClass.branch2.str2["+obj.branch2.str2+"]" );
			System.out.println( "TestDataClass.branch2.byte2["+obj.branch2.byte2+"]" );
			System.out.println( "TestDataClass.branch2.short2["+obj.branch2.short2+"]" );
			System.out.println( "TestDataClass.branch2.int2["+obj.branch2.int2+"]" );
			System.out.println( "TestDataClass.branch2.long2["+obj.branch2.long2+"]" );
			System.out.println( "TestDataClass.branch2.float2["+obj.branch2.float2+"]" );
			System.out.println( "TestDataClass.branch2.double2["+obj.branch2.double2+"]" );
			System.out.println( "TestDataClass.branch2.boolean2["+obj.branch2.boolean2+"]" );
		}
		
		System.out.println( "------------------------------ dump TestDataClass.branch3" );
		if( obj.branch3 != null ) {
			System.out.println( "TestDataClass.branch3.str3["+obj.branch3.str3+"]" );
			System.out.println( "TestDataClass.branch3.byte3["+obj.branch3.byte3+"]" );
			System.out.println( "TestDataClass.branch3.short3["+obj.branch3.short3+"]" );
			System.out.println( "TestDataClass.branch3.int3["+obj.branch3.int3+"]" );
			System.out.println( "TestDataClass.branch3.Long3["+obj.branch3.long3+"]" );
			System.out.println( "TestDataClass.branch3.Float3["+obj.branch3.float3+"]" );
			System.out.println( "TestDataClass.branch3.double3["+obj.branch3.double3+"]" );
			System.out.println( "TestDataClass.branch3.boolean3["+obj.branch3.boolean3+"]" );
			
			System.out.println( "------------------------------ dump TestDataClass.branch3.branch33" );
			if( obj.branch3.branch33 != null ) {
				System.out.println( "TestDataClass.branch3.branch33.str33["+obj.branch3.branch33.str33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.byte33["+obj.branch3.branch33.byte33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.short33["+obj.branch3.branch33.short33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.Int33["+obj.branch3.branch33.int33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.long33["+obj.branch3.branch33.long33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.float33["+obj.branch3.branch33.float33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.Double33["+obj.branch3.branch33.double33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.Boolean33["+obj.branch3.branch33.boolean33+"]" );
				System.out.println( "TestDataClass.branch3.branch33.name33["+obj.branch3.branch33.name33+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.array4" );
		if( obj.array4 != null ) {
			Iterator<String> iterArray4 = obj.array4.iterator() ;
			while( iterArray4.hasNext() ) {
				String value = iterArray4.next() ;
				System.out.println( "TestDataClass.array4.<String>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.array5" );
		if( obj.array5 != null ) {
			Iterator<Integer> iterArray5 = obj.array5.iterator() ;
			while( iterArray5.hasNext() ) {
				Integer value = iterArray5.next() ;
				System.out.println( "TestDataClass.array5.<Integer>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.array6" );
		if( obj.array6 != null ) {
			Iterator<Boolean> iterArray6 = obj.array6.iterator() ;
			while( iterArray6.hasNext() ) {
				Boolean value = iterArray6.next() ;
				System.out.println( "TestDataClass.array6.<Boolean>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.array65" );
		if( obj.array65 != null ) {
			Iterator<String> iterArray65 = obj.array65.iterator() ;
			while( iterArray65.hasNext() ) {
				String value = iterArray65.next() ;
				System.out.println( "TestDataClass.array6.<Boolean>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.array7" );
		if( obj.array7 != null ) {
			Iterator<Branch7> iterArray7 = obj.array7.iterator() ;
			while( iterArray7.hasNext() ) {
				Branch7 value = iterArray7.next() ;
				System.out.println( "TestDataClass.array6.<Branch7>.str7["+value.str7+"] .int7["+value.int7+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestDataClass.strings8" );
		if( obj.strings8 != null ) {
			System.out.println( "TestDataClass.strings8.string81["+obj.strings8.getString81()+"]" );
			System.out.println( "TestDataClass.strings8.string82["+obj.strings8.getString82()+"]" );
			System.out.println( "TestDataClass.strings8.string83["+obj.strings8.getString83()+"]" );
			System.out.println( "TestDataClass.strings8.string84["+obj.strings8.getString84()+"]" );
			System.out.println( "TestDataClass.strings8.string85["+obj.strings8.getString85()+"]" );
			System.out.println( "TestDataClass.strings8.string86["+obj.strings8.getString86()+"]" );
			System.out.println( "TestDataClass.strings8.string87["+obj.strings8.getString87()+"]" );
			System.out.println( "TestDataClass.strings8.string88["+obj.strings8.getString88()+"]" );
			System.out.println( "TestDataClass.strings8.string89["+obj.strings8.getString89()+"]" );
		}
		
		System.out.println( "------------------------------ dump TestDataClass.numbers9" );
		if( obj.numbers9 != null ) {
			System.out.println( "TestDataClass.numbers9.long91["+obj.numbers9.getLong91()+"]" );
			System.out.println( "TestDataClass.numbers9.long92["+obj.numbers9.getLong92()+"]" );
			System.out.println( "TestDataClass.numbers9.long93["+obj.numbers9.getLong93()+"]" );
			System.out.println( "TestDataClass.numbers9.floatE94["+obj.numbers9.getFloatE94()+"]" );
			System.out.println( "TestDataClass.numbers9.floatE95["+obj.numbers9.getFloatE95()+"]" );
			System.out.println( "TestDataClass.numbers9.floatE96["+obj.numbers9.getFloatE96()+"]" );
			System.out.println( "TestDataClass.numbers9.doubleE97["+obj.numbers9.getDoubleE97()+"]" );
			System.out.println( "TestDataClass.numbers9.doubleE98["+obj.numbers9.getDoubleE98()+"]" );
			System.out.println( "TestDataClass.numbers9.doubleE99["+obj.numbers9.getDoubleE99()+"]" );
		}
		
		System.out.println( "------------------------------ dump end" );
	}
	
	public static void main(String[] args) {
		TestDataClass		object = new TestDataClass() ;
		
		setTestDataObject( object ) ;
		
		String string = OKJSON.objectToString( object, OKJSON.OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE|OKJSON.OKJSON_OTIONS_PRETTY_FORMAT_ENABLE ) ;
		if( string == null ) {
			System.out.println( "objectToString failed["+OKJSON.getErrorCode()+"]["+OKJSON.getErrorDesc()+"]" );
			return;
		} else {
			System.out.println( "objectToString ok" );
			System.out.println( "jsonString["+string+"]" );
		}
		
		TestDataClass object2 = OKJSON.stringToObject( string, TestDataClass.class, OKJSON.OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
		if( object2 == null ) {
			System.out.println( "stringToObject failed["+OKJSON.getErrorCode()+"]["+OKJSON.getErrorDesc()+"]" );
			return;
		}
		else {
			System.out.println( "stringToObject ok" );
			printTestDataClass( object2 );
		}
		
		return;
	}
}
