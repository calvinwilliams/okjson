package xyz.calvinwilliams.okjson;

import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;

import xyz.calvinwilliams.okjson.TestJsonData;

/**
 * @author calvin
 *
 */
public class Test {
	public static void printObject( TestJsonData obj ) {
		System.out.println( "------------------------------ dump TestJsonData" );
		System.out.println( "TestJsonData.str1["+obj.str1+"]" );
		System.out.println( "TestJsonData.byte1["+obj.byte1+"]" );
		System.out.println( "TestJsonData.short1["+obj.short1+"]" );
		System.out.println( "TestJsonData.int1["+obj.int1+"]" );
		System.out.println( "TestJsonData.long1["+obj.long1+"]" );
		System.out.println( "TestJsonData.float1["+obj.float1+"]" );
		System.out.println( "TestJsonData.double1["+obj.double1+"]" );
		System.out.println( "TestJsonData.boolean1["+obj.boolean1+"]" );
		
		System.out.println( "------------------------------ dump TestJsonData.branch2" );
		if( obj.branch2 != null ) {
			System.out.println( "TestJsonData.branch2.str2["+obj.branch2.str2+"]" );
			System.out.println( "TestJsonData.branch2.byte2["+obj.branch2.byte2+"]" );
			System.out.println( "TestJsonData.branch2.short2["+obj.branch2.short2+"]" );
			System.out.println( "TestJsonData.branch2.int2["+obj.branch2.int2+"]" );
			System.out.println( "TestJsonData.branch2.long2["+obj.branch2.long2+"]" );
			System.out.println( "TestJsonData.branch2.float2["+obj.branch2.float2+"]" );
			System.out.println( "TestJsonData.branch2.double2["+obj.branch2.double2+"]" );
			System.out.println( "TestJsonData.branch2.boolean2["+obj.branch2.boolean2+"]" );
		}
		
		System.out.println( "------------------------------ dump TestJsonData.branch3" );
		if( obj.branch3 != null ) {
			System.out.println( "TestJsonData.branch3.str3["+obj.branch3.str3+"]" );
			System.out.println( "TestJsonData.branch3.byte3["+obj.branch3.byte3+"]" );
			System.out.println( "TestJsonData.branch3.short3["+obj.branch3.short3+"]" );
			System.out.println( "TestJsonData.branch3.int3["+obj.branch3.int3+"]" );
			System.out.println( "TestJsonData.branch3.long3["+obj.branch3.long3+"]" );
			System.out.println( "TestJsonData.branch3.float3["+obj.branch3.float3+"]" );
			System.out.println( "TestJsonData.branch3.double3["+obj.branch3.double3+"]" );
			System.out.println( "TestJsonData.branch3.boolean3["+obj.branch3.boolean3+"]" );
			
			System.out.println( "------------------------------ dump TestJsonData.branch3.branch33" );
			if( obj.branch3.branch33 != null ) {
				System.out.println( "TestJsonData.branch3.branch33.str33["+obj.branch3.branch33.str33+"]" );
				System.out.println( "TestJsonData.branch3.branch33.byte33["+obj.branch3.branch33.byte33+"]" );
				System.out.println( "TestJsonData.branch3.branch33.short33["+obj.branch3.branch33.short33+"]" );
				System.out.println( "TestJsonData.branch3.branch33.int33["+obj.branch3.branch33.int33+"]" );
				System.out.println( "TestJsonData.branch3.branch33.long33["+obj.branch3.branch33.long33+"]" );
				System.out.println( "TestJsonData.branch3.branch33.float33["+obj.branch3.branch33.float33+"]" );
				System.out.println( "TestJsonData.branch3.branch33.double33["+obj.branch3.branch33.double33+"]" );
				System.out.println( "TestJsonData.branch3.branch33.boolean33["+obj.branch3.branch33.boolean33+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestJsonData.list4" );
		if( obj.list4 != null ) {
			Iterator<String> iterList4 = obj.list4.iterator() ;
			while( iterList4.hasNext() ) {
				String value = iterList4.next() ;
				System.out.println( "TestJsonData.list4.<String>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestJsonData.list5" );
		if( obj.list5 != null ) {
			Iterator<Integer> iterList5 = obj.list5.iterator() ;
			while( iterList5.hasNext() ) {
				Integer value = iterList5.next() ;
				System.out.println( "TestJsonData.list5.<Integer>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump TestJsonData.list6" );
		if( obj.list6 != null ) {
			Iterator<Boolean> iterList6 = obj.list6.iterator() ;
			while( iterList6.hasNext() ) {
				Boolean value = iterList6.next() ;
				System.out.println( "TestJsonData.list6.<Boolean>.["+value+"]" );
			}
		}
		
		System.out.println( "------------------------------ dump end" );
	}
	
	public static void testStringToObject( char[] json ) {
		
		OkJson okJson = new OkJson() ;
		// OkJson.setStrictPolicy(true);
		TestJsonData obj = okJson.stringToObject( json, TestJsonData.class ) ;
		if( obj == null ) {
			System.out.println( "stringToObject failed["+okJson.getErrorCode()+"]["+okJson.getErrorDesc()+"]" );
			return;
		}
		else {
			System.out.println( "stringToObject ok" );
			printObject( obj );
		}
		
		return;
	}
	
	public static void pressStringToObject( char[] json ) {
		
		long l , count = 10000000 ;
		long beginMillisSecondstamp = System.currentTimeMillis() ;
		
		for( l = 0 ; l < count ; l++ ) {
			OkJson okJson = new OkJson() ;
			// OkJson.setStrictPolicy(true);
			TestJsonData obj = okJson.stringToObject( json, TestJsonData.class ) ;
			if( obj == null ) {
				System.out.println( "stringToObject failed["+okJson.getErrorCode()+"]" );
				return;
			}
			else if( l == 0 ){
				System.out.println( "stringToObject ok" );
				printObject( obj );
			}
		}
		
		long endMillisSecondstamp = System.currentTimeMillis() ;
		double diffMillisSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
		System.out.println( "count["+count+"] elapse["+diffMillisSecond+"]ms" );
		
		return;
	}
	
	public static void testJsonFromString() {
		String str = "{\n"
						+ "	\"str1\" : \"value1\" ,\n"
						+ "	\"int1\" : 123 ,\n"
						+ "	\"double1\" : 456.789\n"
						+ "}\n" ;
		char[] json = str.toCharArray() ;
		testStringToObject(json);
		return;
	}
	
	public static void testJsonFromFile() {
		File file = new File( "test_basic.json" ) ;
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
		String str = new String(json) ;
		testStringToObject(str.toCharArray());
		return;
	}
	
	public static void pressJsonFromFile() {
		File file = new File( "test_basic.json" ) ;
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
		String str = new String(json) ;
		pressStringToObject(str.toCharArray());
		return;
	}
	
	public static void main(String[] args) {
		testJsonFromFile();
		return;
	}
}