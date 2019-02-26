package xyz.calvinwilliams.okjson;

import java.util.*;
import java.lang.reflect.*;

public class OkJson {

	enum TokenType {
		TOKEN_TYPE_LEFT_BRACE , // {
		TOKEN_TYPE_RIGHT_BRACE , // }
		TOKEN_TYPE_LEFT_BRACKET, // [
		TOKEN_TYPE_RIGHT_BRACKET, // ]
		TOKEN_TYPE_COLON, // :
		TOKEN_TYPE_COMMA, // ;
		TOKEN_TYPE_STRING, // "ABC"
		TOKEN_TYPE_INTEGER, // 123
		TOKEN_TYPE_DECIMAL, // 123.456
		TOKEN_TYPE_BOOL, // true or false
		TOKEN_TYPE_NULL // null
	}
	
	private static ThreadLocal<HashMap<String,HashMap<String,Field>>>	stringMapFieldsCache ;
	private static ThreadLocal<HashMap<String,HashMap<String,Method>>>	stringMapMethodsCache ;
	
	private boolean				strictPolicyEnable ;
	private boolean				directSetPropertyEnable ;
	
	private int					jsonOffset ;
	private int					jsonLength ;
	
	private TokenType			tokenType ;
	private int					beginOffset ;
	private int					endOffset ;
	private boolean				booleanValue ;

	private int					errorCode ;
	private String				errorDesc ;
	
	final private static int	OKJSON_ERROR_END_OF_BUFFER = 1 ;
	final private static int	OKJSON_ERROR_INVALID_BYTE = -11 ;
	final private static int	OKJSON_ERROR_NAME_INVALID = -12 ;
	final private static int	OKJSON_ERROR_EXPECT_COLON_AFTER_NAME = -13 ;
	final private static int	OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT = -14 ;
	final private static int	OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT = -15 ;
	final private static int	OKJSON_ERROR_FIND_FIRST_LEFT_BRACE = -16 ;
	final private static int	OKJSON_ERROR_EXCEPTION = -17 ;
	final private static int	OKJSON_ERROR_UNEXPECT = -18 ;
	final private static int	OKJSON_ERROR_NEW_OBJECT = -19 ;
	
	/* example >>>
	 * {
	 * 	"str1" : "value1" ,
	 * 	"int1" : 123 ,
	 * 	"float1" : 456.789
	 * }
	 */
	private int tokenJsonNumber( char[] jsonCharArray ) {
		char	ch ;
		boolean	decimalPointFlag ;
		
		beginOffset = jsonOffset ;
		decimalPointFlag = false ;
		while( jsonOffset < jsonLength ) {
			ch = jsonCharArray[jsonOffset] ;
			if( '0' <= ch && ch <= '9' ) {
				jsonOffset++;
			}
			else if( ch == '.' ) {
				decimalPointFlag = true ;
				jsonOffset++;
			}
			else
			{
				if( decimalPointFlag == true )
					tokenType = TokenType.TOKEN_TYPE_DECIMAL ;
				else
					tokenType = TokenType.TOKEN_TYPE_INTEGER ;
				endOffset = jsonOffset-1 ;
				jsonOffset++;
				return 0;
			}
		}
		return OKJSON_ERROR_END_OF_BUFFER;
	}
	
	private int tokenJsonWord( char[] jsonCharArray ) {
		char	ch ;
		int		nret = 0 ;
		
		while( jsonOffset < jsonLength ) {
			ch = jsonCharArray[jsonOffset] ;
			if( ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' ) {
				jsonOffset++;
			}
			else if( ch == '{' ) {
				tokenType = TokenType.TOKEN_TYPE_LEFT_BRACE ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			}
			else if( ch == '}'  ) {
				tokenType = TokenType.TOKEN_TYPE_RIGHT_BRACE ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			}
			else if( ch == '['  ) {
				tokenType = TokenType.TOKEN_TYPE_LEFT_BRACKET ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			}
			else if( ch == ']'  ) {
				tokenType = TokenType.TOKEN_TYPE_RIGHT_BRACKET ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			}
			else if( ch == '"'  ) {
				jsonOffset++;
				beginOffset = jsonOffset ;
				while( jsonOffset < jsonLength ) {
					ch = jsonCharArray[jsonOffset] ;
					if( ch == '"' ) {
						tokenType = TokenType.TOKEN_TYPE_STRING ;
						endOffset = jsonOffset-1 ;
						jsonOffset++;
						return 0;
					}
					jsonOffset++;
				}
				if( jsonOffset >= jsonLength )
					return OKJSON_ERROR_END_OF_BUFFER;
				
				return 0;
			}
			else if( ch == ':'  ) {
				tokenType = TokenType.TOKEN_TYPE_COLON ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			}
			else if( ch == ','  ) {
				tokenType = TokenType.TOKEN_TYPE_COMMA ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			}
			else if( ch == '-' || ( '0' <= ch && ch <= '9' ) ) {
				if( ch == '-' )
					jsonOffset++;
				
				nret = tokenJsonNumber( jsonCharArray ) ;
				if( nret != 0 )
					return nret;
				
				return 0;
			}
			else if( ch == 't' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'r' ) {
					jsonOffset++;
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'u' ) {
						jsonOffset++;
						ch = jsonCharArray[jsonOffset] ;
						if( ch == 'e' ) {
							tokenType = TokenType.TOKEN_TYPE_BOOL ;
							booleanValue = true ;
							endOffset = jsonOffset ;
							jsonOffset++;
							return 0;
						}
					}
				}
			}
			else if( ch == 'f' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'a' ) {
					jsonOffset++;
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'l' ) {
						jsonOffset++;
						ch = jsonCharArray[jsonOffset] ;
						if( ch == 's' ) {
							jsonOffset++;
							ch = jsonCharArray[jsonOffset] ;
							if( ch == 'e' ) {
								tokenType = TokenType.TOKEN_TYPE_BOOL ;
								booleanValue = false ;
								endOffset = jsonOffset ;
								jsonOffset++;
								return 0;
							}
						}
					}
				}
			}
			else if( ch == 'n' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'u' ) {
					jsonOffset++;
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'l' ) {
						jsonOffset++;
						ch = jsonCharArray[jsonOffset] ;
						if( ch == 'l' ) {
							tokenType = TokenType.TOKEN_TYPE_NULL ;
							booleanValue = true ;
							endOffset = jsonOffset ;
							jsonOffset++;
							return 0;
						}
					}
				}
			}
			else
			{
				errorDesc = "Invalid byte '" + ch + "'" ;
				return OKJSON_ERROR_INVALID_BYTE;
			}
		}
		return OKJSON_ERROR_END_OF_BUFFER;
	}
	
	@SuppressWarnings("unchecked")
	private int addList( char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object object, Field field ) {

		try {
			Class<?> typeClazz = field.getType() ;
			// Object typeObject = typeClazz.newInstance() ;
			// if( typeObject instanceof List) {
			if( typeClazz == ArrayList.class || typeClazz == LinkedList.class ) {
				Type type = field.getGenericType() ;
				ParameterizedType pt = (ParameterizedType) type ;
				Class<?> typeClass = (Class<?>) pt.getActualTypeArguments()[0] ;
				if( typeClass == String.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_STRING ) {
						String value = new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1) ;
						((List<Object>) object).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Byte.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Byte value = Byte.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Short.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Short value = Short.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Integer.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Integer value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Long.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Long value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Float.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
						Float value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Double.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
						Double value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Boolean.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
						((List<Object>) object).add( booleanValue );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
					;
				}
				else {
					if( strictPolicyEnable == true )
						return OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
				}
			} else {
				if( strictPolicyEnable == true )
					return OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return OKJSON_ERROR_EXCEPTION;
		}

		return 0;
	}

	private int stringToListObject( char[] jsonCharArray, Object object, Field field ) {
		
		int					nret ;
		
		while(true) {
			// token "value"
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( object != null ) {
				nret = addList( jsonCharArray, tokenType, beginOffset, endOffset, object, field ) ;
				if( nret != 0 )
					return nret;
			}
			
			// token ',' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_COMMA ) {
				continue;
			} else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				break;
			} else {
				int beginPos = endOffset - 16 ;
				if( beginPos < 0 )
					beginPos = 0 ;
				errorDesc = "expect ':' but \"" + String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1) + "\"" ;
				return OKJSON_ERROR_EXPECT_COLON_AFTER_NAME;
			}
			
		}
		
		return 0;
	}
		
	private int setProperty( char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object obj, Field field, Method method ) {
		
		if( field.getType() == String.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_STRING ) {
				try {
					String value = new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1) ;
					if( method != null ) {
						method.setAccessible(true);
						method.invoke(obj, value);
					}
					else if( directSetPropertyEnable == true )
					{
						field.setAccessible(true);
						field.set( obj, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
			else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.setAccessible(true);
						method.invoke(obj, null);
					}
					else if( directSetPropertyEnable == true )
					{
						field.setAccessible(true);
						field.set( obj, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		}
		else if( field.getType().getName().equals("byte") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				byte	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).byteValue() ;
				if( method != null ) {
					method.setAccessible(true);
					method.invoke(obj, value);
				}
				else if( directSetPropertyEnable == true )
				{
					field.setAccessible(true);
					field.setByte( obj, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		}
		else if( field.getType().getName().equals("short") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				short	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).shortValue() ;
				if( method != null ) {
					method.setAccessible(true);
					method.invoke(obj, value);
				}
				else if( directSetPropertyEnable == true )
				{
					field.setAccessible(true);
					field.setShort( obj, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		}
		else if( field.getType().getName().equals("int") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				int	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).intValue() ;
				if( method != null ) {
					method.setAccessible(true);
					method.invoke(obj, value);
				}
				else if( directSetPropertyEnable == true )
				{
					field.setAccessible(true);
					field.setInt( obj, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		}
		else if( field.getType().getName().equals("long") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				long	value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).longValue() ;
				if( method != null ) {
					method.setAccessible(true);
					method.invoke(obj, value);
				}
				else if( directSetPropertyEnable == true )
				{
					field.setAccessible(true);
					field.setLong( obj, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		}
		else if( field.getType().getName().equals("float") && valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
			try {
				float	value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).floatValue() ;
				if( method != null ) {
					method.setAccessible(true);
					method.invoke(obj, value);
				}
				else if( directSetPropertyEnable == true )
				{
					field.setAccessible(true);
					field.setFloat( obj, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		}
		else if( field.getType().getName().equals("double") && valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
			try {
				double	value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).doubleValue() ;
				if( method != null ) {
					method.setAccessible(true);
					method.invoke(obj, value);
				}
				else if( directSetPropertyEnable == true )
				{
					field.setAccessible(true);
					field.setDouble( obj, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		}
		else if( field.getType().getName().equals("boolean") && valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
			try {
				if( method != null ) {
					method.setAccessible(true);
					method.invoke(obj, booleanValue);
				}
				else if( directSetPropertyEnable == true )
				{
					field.setAccessible(true);
					field.setBoolean( obj, booleanValue );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		}
		else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
			try {
				if( method != null ) {
					method.setAccessible(true);
					method.invoke(obj, null);
				}
				else if( directSetPropertyEnable == true )
				{
					field.setAccessible(true);
					field.set( obj, null );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		}
		else {
			if( strictPolicyEnable == true )
				return OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
		}
		
		return 0;
	}
	
	private int stringToObjectProperties( char[] jsonCharArray, Object object ) {
		
		Class					clazz ;
		HashMap<String,Field>	stringMapFields ;
		HashMap<String,Method>	stringMapMethods ;
		Field[]					fields ;
		Field					field ;
		Method					method ;
		int						nameBeginOffset ;
		int						nameEndOffset ;
		String					name ;
		TokenType				valueTokenType ;
		int						valueBeginOffset ;
		int						valueEndOffset ;
		
		int						nret ;
		
		if( object != null ) {
			clazz = object.getClass();
			
			stringMapFields = stringMapFieldsCache.get().get( clazz.getName() ) ;
			if( stringMapFields == null ) {
				// System.out.println("no class["+clazz.getName()+"] fields cache");
				stringMapFields = new HashMap<String,Field>() ;
				stringMapFieldsCache.get().put( clazz.getName(), stringMapFields ) ;
			} else {
				// System.out.println("yes , class["+clazz.getName()+"] fields cached");
			}
			
			stringMapMethods = stringMapMethodsCache.get().get( clazz.getName() ) ;
			if( stringMapMethods == null ) {
				// System.out.println("no class["+clazz.getName()+"] methods cache");
				stringMapMethods = new HashMap<String,Method>() ;
				stringMapMethodsCache.get().put( clazz.getName(), stringMapMethods ) ;
			} else {
				// System.out.println("yes , class["+clazz.getName()+"] methods cached");
			}
			
			if( stringMapFields.isEmpty() ) {
				fields = clazz.getDeclaredFields() ;
				for( Field f : fields ) {
					name = f.getName();

					// System.out.println("add field["+f.getName()+"] to class["+clazz.getName()+"] cache");
					stringMapFields.put(name, f);

					try {
						method = clazz.getDeclaredMethod( "set" + name.substring(0,1).toUpperCase(Locale.getDefault()) + name.substring(1), f.getType() ) ;
						// System.out.println("add method["+method.getName()+"] to class["+clazz.getName()+"] cache");
						stringMapMethods.put(name, method);
					} catch (NoSuchMethodException e2) {
						;
					} catch (SecurityException e2) {
						;
					}
				}
			} else {
				// System.out.println("reuse fields and methods in class["+clazz.getName()+"] cache");
			}
		} else {
			stringMapFields = null ;
			stringMapMethods = null ;
		}
		
		while(true) {
			// token "name"
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			nameBeginOffset = beginOffset ;
			nameEndOffset = endOffset ;
			name = new String(jsonCharArray,nameBeginOffset,nameEndOffset-nameBeginOffset+1) ;
			
			if( object != null ) {
				field = stringMapFields.get(name) ;
				if( field == null ) {
					if( strictPolicyEnable == true )
						return OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT;
				}

				method = stringMapMethods.get(name) ;
			} else {
				field = null ;
				method = null ;
			}
			
			if( tokenType != TokenType.TOKEN_TYPE_STRING ) {
				errorDesc = "name[" + String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1) + "] is not a string " ;
				return OKJSON_ERROR_NAME_INVALID;
			}
			
			// token ':' or ',' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_COLON ) {
				;
			}
			else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				break;
			}
			else {
				int beginPos = endOffset - 16 ;
				if( beginPos < 0 )
					beginPos = 0 ;
				errorDesc = "expect ':' but \"" + String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1) + "\"" ;
				return OKJSON_ERROR_EXPECT_COLON_AFTER_NAME;
			}
			
			// token '{' or '[' or "value"
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			valueTokenType = tokenType ;
			valueBeginOffset = beginOffset ;
			valueEndOffset = endOffset ;
			
			if( tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE || tokenType == TokenType.TOKEN_TYPE_LEFT_BRACKET ) {
				try {
					Object childObject ;
					
					if( field != null ) {
						childObject = field.getType().newInstance() ;
						if( childObject == null )
							return OKJSON_ERROR_UNEXPECT;
					} else {
						childObject = null ;
					}
					
					if( tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE ) {
						nret = stringToObjectProperties( jsonCharArray, childObject ) ;
					} else {
						nret = stringToListObject( jsonCharArray, childObject, field ) ;
					}
					if( nret != 0 )
						return nret;
					
					if( field != null ) {
						field.set( object, childObject );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
			else {
				if( object != null ) {
					nret = setProperty( jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset, object, field, method ) ;
					if( nret != 0 )
						return nret;
				}
			}
			
			// token ',' or '}' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			if( tokenType == TokenType.TOKEN_TYPE_COMMA ) {
				;
			}
			else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE ) {
				break;
			}
			else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				break;
			}
			else {
				int beginPos = endOffset - 16 ;
				if( beginPos < 0 )
					beginPos = 0 ;
				errorDesc = "expect ',' or '}' or ']' but \"" + String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1) + "\"" ;
				return OKJSON_ERROR_EXPECT_COLON_AFTER_NAME;
			}
		}
		
		return 0;
	}
	
	@SuppressWarnings("unused")
	public <T> T stringToObject( String jsonString, Class<T> clazz ) {
		
		HashMap<String,Object>	stringMapFields ;
		HashMap<String,Object>	stringMapMethods ;
		
		char[]	jsonCharArray = jsonString.toCharArray() ;
		
		T		obj ;
		
		if( stringMapFieldsCache == null ) {
			stringMapFieldsCache = new ThreadLocal<HashMap<String,HashMap<String,Field>>>() ;
			if( stringMapFieldsCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			stringMapFieldsCache.set(new HashMap<String,HashMap<String,Field>>());
		}
		
		if( stringMapMethodsCache == null ) {
			stringMapMethodsCache = new ThreadLocal<HashMap<String,HashMap<String,Method>>>() ;
			if( stringMapMethodsCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			stringMapMethodsCache.set(new HashMap<String,HashMap<String,Method>>());
		}
		
		jsonOffset = 0 ;
		jsonLength = jsonCharArray.length ;
		
		errorCode = tokenJsonWord( jsonCharArray ) ;
		if( errorCode != 0 ) {
			return null;
		}
		if( tokenType != TokenType.TOKEN_TYPE_LEFT_BRACE ) {
			errorCode = OKJSON_ERROR_FIND_FIRST_LEFT_BRACE ;
			return null;
		}
		
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		errorCode = stringToObjectProperties( jsonCharArray, obj ) ;
		if( errorCode != 0 )
			return null;
		
		return obj;
	}
	
	public String objectToString() {
		return "";
	}
	
	public void setDirectSetPropertyEnable( boolean b ) {
		directSetPropertyEnable = b ;
	}
	
	public void setStrictPolicy( boolean b ) {
		strictPolicyEnable = b ;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public String getErrorDesc() {
		return errorDesc;
	}
	
	public OkJson() {
		strictPolicyEnable = false ;
		errorCode = 0 ;
	}
}
