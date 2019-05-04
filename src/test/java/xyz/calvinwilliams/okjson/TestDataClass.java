/*
 * okjson - A easy JSON parser/generator for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.okjson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class TestDataClass {
	public String		str1 ;
	public byte			byte1 ;
	public short		short1 ;
	public int			int1 ;
	public Long			long1 ;
	public Float		float1 ;
	public double		double1 ;
	public boolean		boolean1 ;
	public String		null1 ;
	public String		name1 ;
	private String		isnotAccessible ;
	
	public String getName1() {
		System.out.println( "getName1" );
		return name1;
	}
	public void setName1(String name1) {
		System.out.println( "setName1" );
		this.name1 = name1;
	}
	
	public TestDataBranch2		branch2 ;
	
	public TestDataBranch3		branch3 ;
	
	public LinkedList<String>	array4 ;
	public ArrayList<Integer>	array5 ;
	public LinkedList<Boolean>	array6 ;
	public LinkedList<String>	array65 ;
	
	public ArrayList<TestDataBranch7>	array7 ;
	
	public TestDataStrings8				strings8 ;
	public TestDataNumbers9				numbers9 ;
	
	@OkJsonDateTimeFormatter(format="yyyy-MM-dd")
	public LocalDate			localDate10 ;
	
	@OkJsonDateTimeFormatter(format="HH:mm:ss")
	public LocalTime			localTime11 ;
	
	@OkJsonDateTimeFormatter(format="yyyy-MM-dd HH:mm:ss")
	public LocalDateTime		localDateTime12 ;

	public LocalDate getLocalDate10() {
		return localDate10;
	}
	public void setLocalDate10(LocalDate localDate10) {
		this.localDate10 = localDate10;
	}
	public LocalTime getLocalTime11() {
		return localTime11;
	}
	
	public void setLocalTime11(LocalTime localTime11) {
		this.localTime11 = localTime11;
	}
	
	@OkJsonDateTimeFormatter(format="yyyy-MM-dd HH:mm:ss")
	public LinkedList<LocalDateTime>	list13 ;
}
