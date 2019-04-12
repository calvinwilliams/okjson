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
    - [3.1.1. `OKJSON.stringToObject`](#311-okjsonstringtoobject)
    - [3.1.2. `OKJSON.fileToObject`](#312-okjsonfiletoobject)
    - [3.1.3. `OKJSON.objectToString`](#313-okjsonobjecttostring)
    - [3.1.4. `OKJSON.objectToFile`](#314-okjsonobjecttofile)
    - [3.1.5. `OKJSON.getErrorCode`](#315-okjsongeterrorcode)
    - [3.1.6. `OKJSON.getErrorDesc`](#316-okjsongeterrordesc)
  - [3.2. JSON字段类型与实体类属性类型映射表](#32-json%E5%AD%97%E6%AE%B5%E7%B1%BB%E5%9E%8B%E4%B8%8E%E5%AE%9E%E4%BD%93%E7%B1%BB%E5%B1%9E%E6%80%A7%E7%B1%BB%E5%9E%8B%E6%98%A0%E5%B0%84%E8%A1%A8)
  - [3.3. JSON数组元素类型与实体类属性类型映射表](#33-json%E6%95%B0%E7%BB%84%E5%85%83%E7%B4%A0%E7%B1%BB%E5%9E%8B%E4%B8%8E%E5%AE%9E%E4%BD%93%E7%B1%BB%E5%B1%9E%E6%80%A7%E7%B1%BB%E5%9E%8B%E6%98%A0%E5%B0%84%E8%A1%A8)
- [4. 性能压测](#4-%E6%80%A7%E8%83%BD%E5%8E%8B%E6%B5%8B)
  - [4.1. 准备fastjson](#41-%E5%87%86%E5%A4%87fastjson)
  - [4.2. 准备okjson](#42-%E5%87%86%E5%A4%87okjson)
  - [4.3. 测试案例](#43-%E6%B5%8B%E8%AF%95%E6%A1%88%E4%BE%8B)
  - [4.4. 测试结果](#44-%E6%B5%8B%E8%AF%95%E7%BB%93%E6%9E%9C)
- [5. 后续开发](#5-%E5%90%8E%E7%BB%AD%E5%BC%80%E5%8F%91)
- [6. 关于本项目](#6-%E5%85%B3%E4%BA%8E%E6%9C%AC%E9%A1%B9%E7%9B%AE)
- [7. 关于作者](#7-%E5%85%B3%E4%BA%8E%E4%BD%9C%E8%80%85)

<!-- /TOC -->

# 1. 概述

okjson是用JAVA编写的JSON处理器（JSON解析器+JSON生成器）。

它能帮助开发者把一段JSON文本中的数据映射到实体类中，或由一个实体类生成一段JSON文本。

它小巧，源码只有一个类文件和一个注解类文件，方便架构师嵌入到项目/框架中去。

它高效，比号称全世界最快的fastjson还要快约10%。

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

### 3.1.1. `OKJSON.stringToObject`

| | | |
|---|---|---|
| 方法原型 | <T> T stringToObject( String jsonString, Class<T> clazz, int options ); | |
| 方法说明 | 映射JSON字符串中的字段数据到实体类属性 | |
| 参数 | String jsonString : JSON字符串 | |
| | Class<T> clazz : 实体类类型 | |
| | int options : 映射选项 | OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE : 优先直接赋值属性值，否则优先调用setter赋值属性值 |
| | | OKJSON.OPTIONS_STRICT_POLICY : 当JSON字段类型与实体类属性类型不一致，或JSON字段名在实体类属性列表中找不到等警告事件时中断报错，否则忽视 |
| 返回值 | 不等于null : 映射成功，得到实体类对象 | |
| | 等于null : 映射失败 | |

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

### 3.1.2. `OKJSON.fileToObject`

| | | |
|---|---|---|
| 方法原型 | <T> T fileToObject( String filePath, Class<T> clazz, int options ); | |
| 方法说明 | 映射JSON字符串中的字段数据到实体类属性 | |
| 参数 | String filePath : JSON文件名 | |
| | Class<T> clazz : 实体类类型 | |
| | int options : 映射选项 | 同上 |
| 返回值 | 不等于null : 映射成功，得到实体类对象 | |
| | 等于null : 映射失败 | |

（错误码说明同上）

### 3.1.3. `OKJSON.objectToString`

| | | |
|---|---|---|
| 方法原型 | String objectToString( Object object, int options ); | |
| 方法说明 | 映射实体类属性生成JSON字符串 | |
| 参数 | Object object : 实体类 | |
| | int options : 映射选项 |　OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE : 优先直接赋值属性值，否则优先调用setter赋值属性值 |
| | | OKJSON.OPTIONS_PRETTY_FORMAT_ENABLE : 以缩进风格生成JSON，否则按紧凑风格 |
| 返回值 | 不等于null : 生成JSON字符串成功 | |
| | 等于null : 生成失败 | |

| 错误码 | 错误原因说明 |
|---|---|
| OKJSON_ERROR_END_OF_BUFFER | 不完整的JSON |
| OKJSON_ERROR_EXCEPTION | 发生异常 |
| OKJSON_ERROR_NEW_OBJECT | 创建对象失败 |

### 3.1.4. `OKJSON.objectToFile`

| | | |
|---|---|---|
| 方法原型 | int objectToFile( Object object, String filePath, int options ); | |
| 方法说明 | 映射实体类属性生成JSON字符串写到文件中 | |
| 参数 | Object object : 实体类 | |
| | String filePath : JSON文件名 | |
| | int options : 映射选项 | 同上 |
| 返回值 | 等于0 : 生成或写文件成功 | |
| | 不等于0 : 生成或写文件失败 | |

（错误码说明同上）

### 3.1.5. `OKJSON.getErrorCode`

| | | |
|---|---|---|
| 方法原型 | Integer getErrorCode(); | |
| 方法说明 | 当JSON解析或生成失败后，调用此方法获取错误码 | |
| 返回值 | 最近错误码 | |

### 3.1.6. `OKJSON.getErrorDesc`

| | | |
|---|---|---|
| 方法原型 | String getErrorDesc(); | |
| 方法说明 | 当JSON解析或生成失败后，调用此方法获取错误描述 | |
| 返回值 | 最近错误描述 | |

## 3.2. JSON字段类型与实体类属性类型映射表

| JSON字段类型 | 实体类属性类型 |
|---|---|---|
| 字符串 | String |
| 整型数字 | Byte或byte |
| 整型数字 | Short或short |
| 整型数字 | Integer或int |
| 整型数字 | Long或long |
| 浮点数字 | Float或float |
| 浮点数字 | Double或double |
| 布尔值 | Boolean或boolean |
| 字符串 | LocalDate |
| 字符串 | LocalTime |
| 字符串 | LocalDateTime |
| 数组 | ArrayList |
| 数组 | LinkedList |
| JSON树枝 | JAVA对象 |

## 3.3. JSON数组元素类型与实体类属性类型映射表

| JSON数组元素类型 | 实体类属性类型 |
|---|---|
| 字符串 | String |
| 整型数字 | Byte |
| 整型数字 | Short |
| 整型数字 | Integer |
| 整型数字 | Long |
| 浮点数字 | Float |
| 浮点数字 | Double |
| 布尔值 | Boolean |
| 字符串 | LocalDate |
| 字符串 | LocalTime |
| 字符串 | LocalDateTime |
| JSON树枝 | JAVA对象 |

# 4. 性能压测

## 4.1. 准备fastjson

## 4.2. 准备okjson

## 4.3. 测试案例

## 4.4. 测试结果

# 5. 后续开发

# 6. 关于本项目

# 7. 关于作者

