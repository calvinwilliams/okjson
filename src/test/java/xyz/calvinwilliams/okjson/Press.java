package xyz.calvinwilliams.okjson;

import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;

import xyz.calvinwilliams.okjson.PressDataClass;

/**
 * @author calvin
 *
 */
public class Press {
	
	public static void printPressDataClass( PressDataClass obj ) {
		
		System.out.println( "------------------------------ dump PressDataClass" );
		System.out.println( "DataClass.str1["+obj.str1+"]" );
		System.out.println( "PressDataClass.byte1["+obj.byte1+"]" );
		System.out.println( "PressDataClass.short1["+obj.short1+"]" );
		System.out.println( "PressDataClass.int1["+obj.int1+"]" );
		System.out.println( "PressDataClass.long1["+obj.long1+"]" );
		System.out.println( "PressDataClass.float1["+obj.float1+"]" );
		System.out.println( "PressDataClass.double1["+obj.double1+"]" );
		System.out.println( "PressDataClass.boolean1["+obj.boolean1+"]" );
		System.out.println( "PressDataClass.name1["+obj.name1+"]" );
		
		System.out.println( "------------------------------ dump PressDataClass.press2" );
		if( obj.press2 != null ) {
			System.out.println( "PressDataClass.branch2.str2["+obj.press2.str2+"]" );
			System.out.println( "PressDataClass.branch2.byte2["+obj.press2.byte2+"]" );
			System.out.println( "PressDataClass.branch2.short2["+obj.press2.short2+"]" );
			System.out.println( "PressDataClass.branch2.int2["+obj.press2.int2+"]" );
			System.out.println( "PressDataClass.branch2.long2["+obj.press2.long2+"]" );
			System.out.println( "PressDataClass.branch2.float2["+obj.press2.float2+"]" );
			System.out.println( "PressDataClass.branch2.double2["+obj.press2.double2+"]" );
			System.out.println( "PressDataClass.branch2.boolean2["+obj.press2.boolean2+"]" );
		}
		
		System.out.println( "------------------------------ dump end" );
	}
	
	public static void pressStringToObject( String jsonString ) {
		
		long l , count = 1000000 ;
		long beginMillisSecondstamp = System.currentTimeMillis() ;
		
		for( l = 0 ; l < count ; l++ ) {
			OkJson okjson = new OkJson() ;
			okjson.setDirectSetPropertyEnable( true );
			// OkJson.setStrictPolicy(true);
			PressDataClass obj = okjson.stringToObject( jsonString, PressDataClass.class ) ;
			if( obj == null ) {
				System.out.println( "stringToObject failed["+okjson.getErrorCode()+"]" );
				return;
			}
			else if( l == 0 ){
				System.out.println( "stringToObject ok" );
				printPressDataClass( obj );
			}
		}
		
		long endMillisSecondstamp = System.currentTimeMillis() ;
		double elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
		System.out.println( "count["+count+"] elapse["+elpaseSecond+"]ms" );
		double countPerSecond = count / elpaseSecond ;
		System.out.println( "count per second["+countPerSecond+"]" );
		
		return;
	}
	
	public static void pressJsonFromFile() {
		File file = new File( "press.json" ) ;
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
		pressStringToObject(jsonString);
		return;
	}
	
	public static void main(String[] args) {
		pressJsonFromFile();
		return;
	}
}
