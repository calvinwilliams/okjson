package xyz.calvinwilliams.okjson;

import java.time.format.DateTimeFormatter;

public class Demo {

	public static void printDemoUser( DemoUserClass demoUser ) {
		System.out.println("demoUser.userName["+demoUser.userName+"]");
		System.out.println("demoUser.email["+demoUser.email+"]");
		
		System.out.println("------ demoUser.userExtInfo ------");
		System.out.println("demoUser.userExtInfo.gender["+demoUser.userExtInfo.gender+"]");
		System.out.println("demoUser.userExtInfo.age["+demoUser.userExtInfo.age+"]");
		System.out.println("demoUser.userExtInfo.address["+demoUser.userExtInfo.address+"]");
		
		System.out.println("------ demoUser.interestGroupList ------");
		for( String s : demoUser.interestGroupList ) {
			System.out.println("demoUser.interestGroupList["+s+"]");
		}
		
		System.out.println("------ demoUser.borrowDetailList ------");
		for( BorrowDetail b : demoUser.borrowDetailList ) {
			System.out.println("--- demoUser.borrowDetail ---");
			System.out.println("demoUser.borrowDetailList.bookName["+b.bookName+"]");
			System.out.println("demoUser.borrowDetailList.author["+b.author+"]");
			System.out.println("demoUser.borrowDetailList.borrowDate["+DateTimeFormatter.ofPattern("yyyy-MM-dd").format(b.borrowDate)+"]");
			System.out.println("demoUser.borrowDetailList.borrowTime["+DateTimeFormatter.ofPattern("HH:mm:ss").format(b.borrowTime)+"]");
		}
	}
	
	public static void main(String[] args) {
		DemoUserClass	demoUser = new DemoUserClass() ;
		
		System.out.println( "OKJSON.stringToObject ..." );
		demoUser = OKJSON.fileToObject( "demo.json", DemoUserClass.class, OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
		if( demoUser == null ) {
			System.out.println( "OKJSON.stringToObject failed["+OKJSON.getErrorCode()+"]["+OKJSON.getErrorDesc()+"]" );
			return;
		} else {
			System.out.println( "OKJSON.stringToObject ok" );
			printDemoUser( demoUser );
		}
	}
}
