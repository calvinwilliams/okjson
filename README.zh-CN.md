okjson - JAVA编写的小巧、高效、灵活的JSON处理器（JSON解析器+JSON生成器）
===================================================

<!-- TOC -->

- [okjson - JAVA编写的小巧、高效、灵活的JSON处理器（JSON解析器+JSON生成器）](#okjson---java%E7%BC%96%E5%86%99%E7%9A%84%E5%B0%8F%E5%B7%A7%E9%AB%98%E6%95%88%E7%81%B5%E6%B4%BB%E7%9A%84json%E5%A4%84%E7%90%86%E5%99%A8json%E8%A7%A3%E6%9E%90%E5%99%A8json%E7%94%9F%E6%88%90%E5%99%A8)
- [1. 概述](#1-%E6%A6%82%E8%BF%B0)
- [2. 一个示例](#2-%E4%B8%80%E4%B8%AA%E7%A4%BA%E4%BE%8B)
	- [2.1. 编写JSON文件](#21-%E7%BC%96%E5%86%99json%E6%96%87%E4%BB%B6)
	- [2.2. 编写实体类](#22-%E7%BC%96%E5%86%99%E5%AE%9E%E4%BD%93%E7%B1%BB)
	- [2.3. 编写示例代码](#23-%E7%BC%96%E5%86%99%E7%A4%BA%E4%BE%8B%E4%BB%A3%E7%A0%81)
- [3. 使用参考](#3-%E4%BD%BF%E7%94%A8%E5%8F%82%E8%80%83)
	- [3.1. 静态方法](#31-%E9%9D%99%E6%80%81%E6%96%B9%E6%B3%95)
		- [3.1.1. `OKJSON.getErrorCode`](#311-okjsongeterrorcode)
		- [3.1.2. `OKJSON.getErrorDesc`](#312-okjsongeterrordesc)
		- [3.1.3. `OKJSON.stringToObject`](#313-okjsonstringtoobject)
		- [3.1.4. `OKJSON.fileToObject`](#314-okjsonfiletoobject)
		- [3.1.5. `OKJSON.objectToString`](#315-okjsonobjecttostring)
		- [3.1.6. `OKJSON.objectToFile`](#316-okjsonobjecttofile)
	- [3.2. JSON字段类型与实体类属性类型映射表](#32-json%E5%AD%97%E6%AE%B5%E7%B1%BB%E5%9E%8B%E4%B8%8E%E5%AE%9E%E4%BD%93%E7%B1%BB%E5%B1%9E%E6%80%A7%E7%B1%BB%E5%9E%8B%E6%98%A0%E5%B0%84%E8%A1%A8)
	- [3.3. JSON数组简单元素类型与实体类属性类型映射表](#33-json%E6%95%B0%E7%BB%84%E7%AE%80%E5%8D%95%E5%85%83%E7%B4%A0%E7%B1%BB%E5%9E%8B%E4%B8%8E%E5%AE%9E%E4%BD%93%E7%B1%BB%E5%B1%9E%E6%80%A7%E7%B1%BB%E5%9E%8B%E6%98%A0%E5%B0%84%E8%A1%A8)
- [4. 性能压测](#4-%E6%80%A7%E8%83%BD%E5%8E%8B%E6%B5%8B)
	- [4.1. 测试环境](#41-%E6%B5%8B%E8%AF%95%E7%8E%AF%E5%A2%83)
	- [4.2. 测试案例](#42-%E6%B5%8B%E8%AF%95%E6%A1%88%E4%BE%8B)
	- [4.3. fastjson压测代码](#43-fastjson%E5%8E%8B%E6%B5%8B%E4%BB%A3%E7%A0%81)
	- [4.4. okjson压测代码](#44-okjson%E5%8E%8B%E6%B5%8B%E4%BB%A3%E7%A0%81)
	- [4.5. 测试过程](#45-%E6%B5%8B%E8%AF%95%E8%BF%87%E7%A8%8B)
	- [4.6. 测试结果](#46-%E6%B5%8B%E8%AF%95%E7%BB%93%E6%9E%9C)
- [5. 后续开发](#5-%E5%90%8E%E7%BB%AD%E5%BC%80%E5%8F%91)
- [6. 关于本项目](#6-%E5%85%B3%E4%BA%8E%E6%9C%AC%E9%A1%B9%E7%9B%AE)
- [7. 关于作者](#7-%E5%85%B3%E4%BA%8E%E4%BD%9C%E8%80%85)

<!-- /TOC -->

# 1. 概述

okjson是用JAVA编写的JSON处理器（JSON解析器+JSON生成器）。

它能帮助开发者把一段JSON文本中的数据映射到实体类中，或由一个实体类生成一段JSON文本。

它小巧，源码只有一个类文件和一个注解类文件，方便架构师嵌入到项目/框架中去。

它高效，比号称全世界最快的fastjson还要快。

它灵活，不对映射实体类有各种各样约束要求。

一个好工具就是简单、朴素的。

# 2. 一个示例

来一个简单示例感受一下（所有代码可在源码包`src\test\java\xyz\calvinwilliams\okjson`里找到）

## 2.1. 编写JSON文件

demo.json
```
{
	"userName" : "Calvin" ,
	"email" : "calvinwilliams@163.com" ,
	"userExtInfo" : {
		"gender" : "M" ,
		"age" : 30 ,
		"address" : "I won't tell you"
	} ,
	"interestGroupList" : [
		"Programing", "Playing game", "Reading", "Sleeping"
	] ,
	"borrowDetailList" : [
		{
			"bookName" : "Thinking in JAVA" ,
			"author" : "Bruce Eckel" ,
			"borrowDate" : "2014-01-02" ,
			"borrowTime" : "17:30:00"
		} ,
		{
			"bookName" : "Thinking in C++" ,
			"author" : "Bruce Eckel too" ,
			"borrowDate" : "2014-02-04" ,
			"borrowTime" : "17:35:00"
		} ,
		{
			"bookName" : "Thinking in okjson" ,
			"author" : "It's me !!!" ,
			"borrowDate" : "2014-03-06" ,
			"borrowTime" : "17:40:00"
		}
	]
}
```

## 2.2. 编写实体类

DemoUserClass.java

```
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
```

## 2.3. 编写示例代码

读入JSON文件，映射所有字段数据到实体类属性中去

```
package xyz.calvinwilliams.okjson;

import java.time.format.DateTimeFormatter;

public class Demo {

	public static void printDemoUser( DemoUserClass demoUser ) {
		...
	}
	
	public static void main(String[] args) {
		DemoUserClass	demoUser = new DemoUserClass() ;
		
		System.out.println( "OKJSON.stringToObject ..." );
		demoUser = OKJSON.fileToObject( "demo.json", DemoUserClass.class, OKJSON.OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
		if( demoUser == null ) {
			System.out.println( "OKJSON.stringToObject failed["+OKJSON.getErrorCode()+"]["+OKJSON.getErrorDesc()+"]" );
			return;
		} else {
			System.out.println( "OKJSON.stringToObject ok" );
			printDemoUser( demoUser );
		}
	}
}
```

调用一个静态方法就能把JSON所有字段数据都映射到实体类属性中去，是不是很简单。

我的其它开源产品都用它装载配置文件，小巧、高效、灵活。

# 3. 使用参考

## 3.1. 静态方法

### 3.1.1. `OKJSON.getErrorCode`

| | |
|---|---|
| 方法原型 | Integer getErrorCode(); |
| 方法说明 | 当JSON解析或生成失败后，调用此方法获取错误码 |
| 返回值 | 最近错误码 |

### 3.1.2. `OKJSON.getErrorDesc`

| | |
|---|---|
| 方法原型 | String getErrorDesc(); |
| 方法说明 | 当JSON解析或生成失败后，调用此方法获取错误描述 |
| 返回值 | 最近错误描述 |

### 3.1.3. `OKJSON.stringToObject`

| | |
|---|---|
| 方法原型 | <T> T stringToObject( String jsonString, Class<T> clazz, int options ); |
| 方法说明 | 映射JSON字符串中的字段数据到实体类属性 |
| 参数 | String jsonString : JSON字符串 |
| | Class<T> clazz : 实体类类型 |
| | int options : 映射选项 |
| 返回值 | 不等于null : 映射成功，得到实体类对象 |
| | 等于null : 映射失败 |

<p />

| 映射选项 | 说明 |
|---|---|
| OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE | 优先直接赋值属性值，否则优先调用setter赋值属性值 |
| OKJSON.OPTIONS_STRICT_POLICY | 当JSON字段类型与实体类属性类型不一致，或JSON字段名在实体类属性列表中找不到等警告事件时中断报错，否则忽视 |

<p />

| 错误码 | 错误原因说明 |
|---|---|
| OKJSON_ERROR_END_OF_BUFFER | 不完整的JSON |
| OKJSON_ERROR_UNEXPECT | 出现不期望的字符 |
| OKJSON_ERROR_EXCEPTION | 发生异常 |
| OKJSON_ERROR_INVALID_BYTE | 无效字符 |
| OKJSON_ERROR_FIND_FIRST_LEFT_BRACE | JSON首个非白字符不是'{' |
| OKJSON_ERROR_NAME_INVALID | JSON的KEY名字非法 |
| OKJSON_ERROR_EXPECT_COLON_AFTER_NAME | 在KEY名字后不是':' |
| OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE | 在'{'后非法分词 |
| OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT | 没有JSON字段类型对应的实体类属性类型 |
| OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT | JSON字段名在实体类属性列表中找不到 |
| OKJSON_ERROR_NEW_OBJECT | 创建对象失败 |

### 3.1.4. `OKJSON.fileToObject`

| | |
|---|---|
| 方法原型 | <T> T fileToObject( String filePath, Class<T> clazz, int options ); |
| 方法说明 | 映射JSON字符串中的字段数据到实体类属性 |
| 参数 | String filePath : JSON文件名 |
| | Class<T> clazz : 实体类类型 |
| | int options : 映射选项 |
| 返回值 | 不等于null : 映射成功，得到实体类对象 |
| | 等于null : 映射失败 |

（映射选项和错误码说明同上）

### 3.1.5. `OKJSON.objectToString`

| | |
|---|---|
| 方法原型 | String objectToString( Object object, int options ); |
| 方法说明 | 映射实体类属性生成JSON字符串 |
| 参数 | Object object : 实体类 |
| | int options : 映射选项 |
| 返回值 | 不等于null : 生成JSON字符串成功 |
| | 等于null : 生成失败 |

<p />

| 映射选项 | 说明 |
|---|---|
| OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE | 优先直接赋值属性值，否则优先调用setter赋值属性值 |
| OKJSON.OPTIONS_PRETTY_FORMAT_ENABLE | 以缩进风格生成JSON，否则按紧凑风格 |

<p />

| 错误码 | 错误原因说明 |
|---|---|
| OKJSON_ERROR_END_OF_BUFFER | 不完整的JSON |
| OKJSON_ERROR_EXCEPTION | 发生异常 |
| OKJSON_ERROR_NEW_OBJECT | 创建对象失败 |

### 3.1.6. `OKJSON.objectToFile`

| | |
|---|---|
| 方法原型 | int objectToFile( Object object, String filePath, int options ); |
| 方法说明 | 映射实体类属性生成JSON字符串写到文件中 |
| 参数 | Object object : 实体类 |
| | String filePath : JSON文件名 |
| | int options : 映射选项 |
| 返回值 | 等于0 : 生成或写文件成功 |
| | 不等于0 : 生成或写文件失败 |

（映射选项和错误码说明同上）

## 3.2. JSON字段类型与实体类属性类型映射表

| JSON字段类型 | JSON示例 | 实体类属性类型 |
|---|---|---|
| 字符串 | "..." | String |
| 整型数字 | 123 | Byte或byte |
| 整型数字 | 123 | Short或short |
| 整型数字 | 123 | Integer或int |
| 整型数字 | 123 | Long或long |
| 浮点数字 | 123.456 | Float或float |
| 浮点数字 | 123.456 | Double或double |
| 布尔值 | true/false | Boolean或boolean |
| 字符串 | "..." | LocalDate |
| 字符串 | "..." | LocalTime |
| 字符串 | "..." | LocalDateTime |
| 数组 | [...] | ArrayList |
| 数组 | [...] |  LinkedList |
| JSON树枝 | {...} | JAVA对象 |

如：
```
public class DemoUserClass {
	String				userName ;
	String				email ;
}
```

如：
```
public class DemoUserClass {
	...
	UserExtInfo			userExtInfo ;
	...
}

class UserExtInfo {
	String				gender ;
	int					age ;
	String				address ;
}
```

如：
```
public class DemoUserClass {
	...
	LinkedList<BorrowDetail>	borrowDetailList ;
}

class BorrowDetail {
	String				bookName ;
	String				author ;
	@OkJsonDateTimeFormatter(format="yyyy-MM-dd")
	LocalDate			borrowDate ;
	@OkJsonDateTimeFormatter(format="HH:mm:ss")
	LocalTime			borrowTime ;
}
```

## 3.3. JSON数组简单元素类型与实体类属性类型映射表

| JSON数组简单元素类型 | JSON示例 | 实体类属性类型 |
|---|---|---|
| 字符串 | "..." | String |
| 整型数字 | 123 | Byte |
| 整型数字 | 123 | Short |
| 整型数字 | 123 | Integer |
| 整型数字 | 123 | Long |
| 浮点数字 | 123.456 | Float |
| 浮点数字 | 123.456 | Double |
| 布尔值 | true/false | Boolean |
| 字符串 | "..." | LocalDate |
| 字符串 | "..." | LocalTime |
| 字符串 | "..." | LocalDateTime |
| JSON树枝 | {...} | JAVA对象 |

如：
```
public class DemoUserClass {
	LinkedList<String>			interestGroupList ;
}
```

# 4. 性能压测

## 4.1. 测试环境

CPU : Intel Core i5-7500 3.4GHz 3.4GHz
Momey : 16GB
OS : WINDOWS 10
JAVA IDE : Eclipse 2018-12

## 4.2. 测试案例

压测对象：okjson v0.0.8.0、fastjson v1.2.56。

压测JSON解析器性能。JSON文本数据映射到实体类各属性里去，交替各压5轮，每轮100万次。

压测JSON生成器性能。由实体类生成JSON文本，交替各压5轮，每轮200万次。

JSON文件`press.json`
```
{
	"str1" : "str1" ,
	"int1" : 1234 ,
	"double1" : 1.234 ,
	"boolean1" : true ,
	
	"press2" : {
		"byte2" : 2 ,
		"short2" : 23 ,
		"long2" : 23456789 ,
		"float2" : 2.345
	}
}
```

实体类`PressDataClass.java`
```
package xyz.calvinwilliams.test_jsonparser;

public class PressDataClass {
	private String			str1 ;
	private int				int1 ;
	private Double			double1 ;
	private boolean			boolean1 ;
	private String			null1 ;
	public PressDataClass2	press2 ;
	
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public int getInt1() {
		return int1;
	}
	public void setInt1(int int1) {
		this.int1 = int1;
	}

	public Double getDouble1() {
		return double1;
	}
	public void setDouble1(Double double1) {
		this.double1 = double1;
	}

	public boolean isBoolean1() {
		return boolean1;
	}
	public void setBoolean1(boolean boolean1) {
		this.boolean1 = boolean1;
	}

	public PressDataClass2 getPress2() {
		return press2;
	}
	public void setPress2(PressDataClass2 press2) {
		this.press2 = press2;
	}
	
	public String getNull1() {
		return null1;
	}
	public void setNull1(String null1) {
		this.null1 = null1;
	}
}
```

实体类`PressDataClass2.java`
```
package xyz.calvinwilliams.test_jsonparser;

public class PressDataClass2 {
	private byte		byte2 ;
	private short		short2 ;
	private Long		long2 ;
	private float		float2 ;
	
	public byte getByte2() {
		return byte2;
	}
	public void setByte2(byte byte2) {
		this.byte2 = byte2;
	}
	
	public short getShort2() {
		return short2;
	}
	public void setShort2(short short2) {
		this.short2 = short2;
	}
	
	public Long getLong2() {
		return long2;
	}
	public void setLong2(Long long2) {
		this.long2 = long2;
	}
	
	public float getFloat2() {
		return float2;
	}
	public void setFloat2(float float2) {
		this.float2 = float2;
	}
}
```

## 4.3. fastjson压测代码

fastjson解析器压测代码`PressFastJsonParser.java`
```
package xyz.calvinwilliams.test_jsonparser;

import java.io.File;
import java.io.FileInputStream;

import com.alibaba.fastjson.*;

public class PressFastJsonParser {

	public static void main(String[] args) {
		
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
		
		long l , count = 1000000 ;
		long beginMillisSecondstamp = System.currentTimeMillis() ;
		
		for( l = 0 ; l < count ; l++ ) {
			PressDataClass obj = JSON.parseObject(jsonString, new TypeReference<PressDataClass>() {}) ;
			if( obj == null ) {
				System.out.println( "JSON.stringToObject failed" );
				return;
			}
			else if( l == 0 ){
				System.out.println( "JSON.stringToObject ok" );
				
				System.out.println( "------------------------------ dump PressDataClass" );
				System.out.println( "DataClass.str1["+obj.getStr1()+"]" );
				System.out.println( "PressDataClass.int1["+obj.getInt1()+"]" );
				System.out.println( "PressDataClass.Double1["+obj.getDouble1()+"]" );
				System.out.println( "PressDataClass.boolean1["+obj.isBoolean1()+"]" );
				
				System.out.println( "------------------------------ dump PressDataClass.press2" );
				if( obj.press2 != null ) {
					System.out.println( "PressDataClass.branch2.byte2["+obj.press2.getByte2()+"]" );
					System.out.println( "PressDataClass.branch2.short2["+obj.press2.getShort2()+"]" );
					System.out.println( "PressDataClass.branch2.Long2["+obj.press2.getLong2()+"]" );
					System.out.println( "PressDataClass.branch2.float2["+obj.press2.getFloat2()+"]" );
				}
			}
		}
		
		long endMillisSecondstamp = System.currentTimeMillis() ;
		double elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
		System.out.println( "count["+count+"] elapse["+elpaseSecond+"]s" );
		double countPerSecond = count / elpaseSecond ;
		System.out.println( "count per second["+countPerSecond+"]" );
		
		return;
	}
}
```

fastjson生成器压测代码`PressFastJsonGenerator.java`
```
package xyz.calvinwilliams.test_jsonparser;

import java.io.File;
import java.io.FileInputStream;

import com.alibaba.fastjson.*;

public class PressFastJsonGenerator {

	public static void main(String[] args) {
		
		PressDataClass	object = new PressDataClass() ;
		
		object.setStr1("str1");
		object.setInt1(1234);
		object.setDouble1(1.234);
		object.setBoolean1(true);
		object.setNull1(null);
		
		object.press2 = new PressDataClass2() ;
		object.press2.setByte2((byte)2);
		object.press2.setShort2((short)23);
		object.press2.setLong2(23456789L);
		object.press2.setFloat2(2.345f);
		
		System.out.println( "------------------------------ dump PressDataClass" );
		System.out.println( "DataClass.str1["+object.getStr1()+"]" );
		System.out.println( "PressDataClass.int1["+object.getInt1()+"]" );
		System.out.println( "PressDataClass.Double1["+object.getDouble1()+"]" );
		System.out.println( "PressDataClass.boolean1["+object.isBoolean1()+"]" );
		System.out.println( "PressDataClass.null1["+object.getNull1()+"]" );
		
		System.out.println( "------------------------------ dump PressDataClass.press2" );
		if( object.press2 != null ) {
			System.out.println( "PressDataClass.branch2.byte2["+object.press2.getByte2()+"]" );
			System.out.println( "PressDataClass.branch2.short2["+object.press2.getShort2()+"]" );
			System.out.println( "PressDataClass.branch2.Long2["+object.press2.getLong2()+"]" );
			System.out.println( "PressDataClass.branch2.float2["+object.press2.getFloat2()+"]" );
		}
		
		long l , count = 5000000 ;
		long beginMillisSecondstamp = System.currentTimeMillis() ;
		
		for( l = 0 ; l < count ; l++ ) {
			String jsonString = JSON.toJSONString( object ) ;
			if( jsonString == null ) {
				System.out.println( "JSON.toJSONString failed" );
				return;
			}
			else if( l == count-1 ){
				System.out.println( "JSON.toJSONString ok" );
				System.out.println( jsonString );
			}
		}
		
		long endMillisSecondstamp = System.currentTimeMillis() ;
		double elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
		System.out.println( "count["+count+"] elapse["+elpaseSecond+"]s" );
		double countPerSecond = count / elpaseSecond ;
		System.out.println( "count per second["+countPerSecond+"]" );
		
		return;
	}
}
```

## 4.4. okjson压测代码

okjson解析器压测代码`PressOkJsonParser.java`
```
package xyz.calvinwilliams.test_jsonparser;

import java.io.File;
import java.io.FileInputStream;

import xyz.calvinwilliams.okjson.*;
import xyz.calvinwilliams.test_jsonparser.PressDataClass;

public class PressOkJsonParser {

	public static void main(String[] args) {
		
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
		
		long l , count = 1000000 ;
		long beginMillisSecondstamp = System.currentTimeMillis() ;
		
		for( l = 0 ; l < count ; l++ ) {
			PressDataClass object = OKJSON.stringToObject( jsonString, PressDataClass.class, OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
			if( object == null ) {
				System.out.println( "okjson.stringToObject failed["+OKJSON.getErrorCode()+"]" );
				return;
			} else if( l == 0 ){
				System.out.println( "okjson.stringToObject ok" );
				
				System.out.println( "------------------------------ dump PressDataClass" );
				System.out.println( "DataClass.str1["+object.getStr1()+"]" );
				System.out.println( "PressDataClass.int1["+object.getInt1()+"]" );
				System.out.println( "PressDataClass.Double1["+object.getDouble1()+"]" );
				System.out.println( "PressDataClass.boolean1["+object.isBoolean1()+"]" );
				
				System.out.println( "------------------------------ dump PressDataClass.press2" );
				if( object.press2 != null ) {
					System.out.println( "PressDataClass.branch2.byte2["+object.press2.getByte2()+"]" );
					System.out.println( "PressDataClass.branch2.short2["+object.press2.getShort2()+"]" );
					System.out.println( "PressDataClass.branch2.Long2["+object.press2.getLong2()+"]" );
					System.out.println( "PressDataClass.branch2.float2["+object.press2.getFloat2()+"]" );
				}
			}
		}
		
		long endMillisSecondstamp = System.currentTimeMillis() ;
		double elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
		System.out.println( "count["+count+"] elapse["+elpaseSecond+"]s" );
		double countPerSecond = count / elpaseSecond ;
		System.out.println( "count per second["+countPerSecond+"]" );
		
		return;
	}
}
```

okjson生成器压测代码`PressOkJsonGenerator.java`
```
package xyz.calvinwilliams.test_jsonparser;

import java.io.File;
import java.io.FileInputStream;

import xyz.calvinwilliams.okjson.OKJSON;
import xyz.calvinwilliams.test_jsonparser.PressDataClass;

public class PressOkJsonGenerator {

	public static void main(String[] args) {
		
		PressDataClass	object = new PressDataClass() ;
		
		object.setStr1("str1");
		object.setInt1(1234);
		object.setDouble1(1.234);
		object.setBoolean1(true);
		object.setNull1(null);
		
		object.press2 = new PressDataClass2() ;
		object.press2.setByte2((byte)2);
		object.press2.setShort2((short)23);
		object.press2.setLong2(23456789L);
		object.press2.setFloat2(2.345f);
		
		System.out.println( "------------------------------ dump PressDataClass" );
		System.out.println( "DataClass.str1["+object.getStr1()+"]" );
		System.out.println( "PressDataClass.int1["+object.getInt1()+"]" );
		System.out.println( "PressDataClass.Double1["+object.getDouble1()+"]" );
		System.out.println( "PressDataClass.boolean1["+object.isBoolean1()+"]" );
		System.out.println( "PressDataClass.null1["+object.getNull1()+"]" );
		
		System.out.println( "------------------------------ dump PressDataClass.press2" );
		if( object.press2 != null ) {
			System.out.println( "PressDataClass.branch2.byte2["+object.press2.getByte2()+"]" );
			System.out.println( "PressDataClass.branch2.short2["+object.press2.getShort2()+"]" );
			System.out.println( "PressDataClass.branch2.Long2["+object.press2.getLong2()+"]" );
			System.out.println( "PressDataClass.branch2.float2["+object.press2.getFloat2()+"]" );
		}
		
		long l , count = 5000000 ;
		long beginMillisSecondstamp = System.currentTimeMillis() ;
		
		for( l = 0 ; l < count ; l++ ) {
			String jsonString = OKJSON.objectToString( object, 0 ) ;
			if( jsonString == null ) {
				System.out.println( "okjson.stringToObject failed["+OKJSON.getErrorCode()+"]["+OKJSON.getErrorDesc()+"]" );
				return;
			} else if( l == count-1 ){
				System.out.println( "okjson.stringToObject ok" );
				System.out.println( jsonString );
			}
		}
		
		long endMillisSecondstamp = System.currentTimeMillis() ;
		double elpaseSecond = (endMillisSecondstamp-beginMillisSecondstamp)/1000.0 ;
		System.out.println( "count["+count+"] elapse["+elpaseSecond+"]s" );
		double countPerSecond = count / elpaseSecond ;
		System.out.println( "count per second["+countPerSecond+"]" );
		
		return;
	}
}
```

## 4.5. 测试过程

JSON解析器性能

okjson
```
count[1000000] elapse[1.446]s
count per second[691562.9322268326]
```

fastjson
```
count[1000000] elapse[2.616]s
count per second[382262.996941896]
```

okjson
```
count[1000000] elapse[1.429]s
count per second[699790.0629811056]
```

fastjson
```
count[1000000] elapse[2.547]s
count per second[392618.767177071]
```

okjson
```
count[1000000] elapse[1.42]s
count per second[704225.3521126761]
```

fastjson
```
count[1000000] elapse[2.473]s
count per second[404367.1653861707]
```

okjson
```
count[1000000] elapse[1.432]s
count per second[698324.0223463688]
```

fastjson
```
count[1000000] elapse[2.48]s
count per second[403225.8064516129]
```

okjson
```
count[1000000] elapse[1.434]s
count per second[697350.069735007]
```

fastjson
```
count[1000000] elapse[2.459]s
count per second[406669.37779585196]
```

JSON生成器性能

okjson
```
count[2000000] elapse[1.399]s
count per second[1429592.5661186562]
```

fastjson
```
count[2000000] elapse[1.51]s
count per second[1324503.311258278]
```

okjson
```
count[2000000] elapse[1.347]s
count per second[1484780.9948032666]
```

fastjson
```
count[2000000] elapse[1.507]s
count per second[1327140.0132714002]
```

okjson
```
count[2000000] elapse[1.364]s
count per second[1466275.6598240468]
```

fastjson
```
count[2000000] elapse[1.403]s
count per second[1425516.7498218103]
```

okjson
```
count[2000000] elapse[1.363]s
count per second[1467351.4306676448]
```

fastjson
```
count[2000000] elapse[1.512]s
count per second[1322751.3227513228]
```

okjson
```
count[2000000] elapse[1.37]s
count per second[1459854.01459854]
```

fastjson
```
count[2000000] elapse[1.409]s
count per second[1419446.4158978]
```

## 4.6. 测试结果

JSON解析器性能曲线图：

![json_parsers_benchmark.png](json_parsers_benchmark.png)

JSON生成器性能曲线图：

![json_generators_benchmark.png](json_generators_benchmark.png)

说明：

* 在JSON解析/反序列化处理性能上，okjson比fastjson快了近一倍(75%)。
* 在JSON生成/序列化处理性能上，okjson比fastjson快了少许(7%)

其它说明：
* fastjson静态类名为JSON取名不好，容易和其它JSON库冲突，或许温少就是这么霸气。okjson静态类名为OKJSON，比较低调 :)
* 实体类中加了很多setter和getter是为了fastjson，okjson并不需要。okjson甚至连实体类构造方法都没有要求必须有，作为通用框架要兼容各种各样的场景。

# 5. 后续开发

* 支持更多字段类型，如老版本JAVA的日期、时间类。
* 增加XML解析器、生成器功能。

# 6. 关于本项目

欢迎使用`okjson`，如果你在使用中碰到了问题请告诉我，谢谢 ^_^

源码托管地址 : [开源中国](https://gitee.com/calvinwilliams/okjson)、[github](https://github.com/calvinwilliams/okjson)

Apache Maven
```
<dependency>
  <groupId>xyz.calvinwilliams</groupId>
  <artifactId>okjson</artifactId>
  <version>0.0.9.0</version>
</dependency>
```

Gradle Kotlin DSL
```
compile("xyz.calvinwilliams:okjson:0.0.9.0")
```

# 7. 关于作者

厉华，左手C，右手JAVA，写过小到性能卓越方便快捷的日志库、HTTP解析器、日志采集器等，大到交易平台/中间件等，分布式系统实践者，容器技术专研者，目前在某城商行负责基础架构。

通过邮箱联系我 : [网易](mailto:calvinwilliams@163.com)、[Gmail](mailto:calvinwilliams.c@gmail.com)
