package xyz.calvinwilliams.okjson;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class DemoUserClass {
	String				userName ;
	String				email ;
	UserExtInfo			userExtInfo ;
	LinkedList<String>			interestGroupList ;
	LinkedList<BorrowDetail>	borrowDetailList ;
}

class UserExtInfo {
	String				gender ;
	int					age ;
	String				address ;
}

class BorrowDetail {
	String				bookName ;
	String				author ;
	@OkJsonDateTimeFormatter(format="yyyy-MM-dd")
	LocalDate			borrowDate ;
	@OkJsonDateTimeFormatter(format="HH:mm:ss")
	LocalTime			borrowTime ;
}
