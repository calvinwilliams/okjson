package xyz.calvinwilliams.okjson;

import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;

import xyz.calvinwilliams.okjson.TestDataClass;

/**
 * @author calvin
 *
 */
public class TestJsonFileToObject {
	
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
	
	public static void testStringToObject( String jsonString ) {
		
		OkJson okjson = new OkJson() ;
		okjson.setDirectAccessPropertyEnable( true );
		// OkJson.setStrictPolicy(true);
		TestDataClass obj = okjson.stringToObject( jsonString, TestDataClass.class ) ;
		if( obj == null ) {
			System.out.println( "stringToObject failed["+okjson.getErrorCode()+"]["+okjson.getErrorDesc()+"]" );
			return;
		}
		else {
			System.out.println( "stringToObject ok" );
			printTestDataClass( obj );
		}
		
		return;
	}
	
	public static void testJsonFileToObject() {
		File file = new File( "test.json" ) ;
		Long fileSize = file.length() ;
		byte[] json = new byte[fileSize.intValue()] ;
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(json);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		String jsonString = new String(json) ;
		testStringToObject(jsonString);
		return;
	}
	
	public static void main(String[] args) {
		testJsonFileToObject();
		return;
	}
}
