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
	
	final private static int	TOKEN_ERROR_END_OF_BUFFER = 1 ;
	final private static int	TOKEN_ERROR_INVALID_BYTE = -11 ;
	final private static int	TOKEN_ERROR_NAME_INVALID = -12 ;
	final private static int	TOKEN_ERROR_EXPECT_COLON_AFTER_NAME = -13 ;
	final private static int	TOKEN_ERROR_NAME_NOT_FOUND_IN_OBJECT = -14 ;
	final private static int	TOKEN_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT = -15 ;
	final private static int	TOKEN_ERROR_FIND_FIRST_LEFT_BRACE = -16 ;
	final private static int	TOKEN_ERROR_EXCEPTION = -17 ;
	final private static int	TOKEN_ERROR_UNEXPECT = -18 ;
	
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
		return TOKEN_ERROR_END_OF_BUFFER;
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
					return TOKEN_ERROR_END_OF_BUFFER;
				
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
				return TOKEN_ERROR_INVALID_BYTE;
			}
		}
		return TOKEN_ERROR_END_OF_BUFFER;
	}
	
	@SuppressWarnings("unchecked")
	private int evalObjectList( char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object obj, Field fld ) {

		try {
			Class<?> typeClazz = fld.getType() ;
			// Object typeObject = typeClazz.newInstance() ;
			// if( typeObject instanceof List) {
			if( typeClazz == ArrayList.class || typeClazz == LinkedList.class ) {
				Type type = fld.getGenericType() ;
				ParameterizedType pt = (ParameterizedType) type ;
				Class<?> typeClass = (Class<?>) pt.getActualTypeArguments()[0] ;
				if( typeClass == String.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_STRING ) {
						String value = new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1) ;
						((List<Object>) obj).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Byte.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Byte value = Byte.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) obj).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Short.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Short value = Short.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) obj).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Integer.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Integer value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) obj).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Long.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Long value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) obj).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Float.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
						Float value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) obj).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Double.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
						Double value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) obj).add( value );
					}
					else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				}
				else if( typeClass == Boolean.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
						/*
						Double value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) obj).add( value );
						*/
						((List<Object>) obj).add( booleanValue );
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
						return TOKEN_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
				}
			} else {
				if( strictPolicyEnable == true )
					return TOKEN_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return TOKEN_ERROR_EXCEPTION;
		}

		return 0;
	}

	private int stringToListObject( char[] jsonCharArray, Object obj, Field fld ) {
		
		TokenType			valueTokenType ;
		int					valueBeginOffset ;
		int					valueEndOffset ;
		
		int					nret ;
		
		while(true) {
			// token "value"
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == TOKEN_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			valueTokenType = tokenType ;
			valueBeginOffset = beginOffset ;
			valueEndOffset = endOffset ;
			
			nret = evalObjectList( jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset, obj, fld ) ;
			if( nret != 0 )
				return nret;
			
			// token ',' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == TOKEN_ERROR_END_OF_BUFFER ) {
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
				return TOKEN_ERROR_EXPECT_COLON_AFTER_NAME;
			}
			
		}
		
		return 0;
	}
		
	private int evalObjectProperty( char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object obj, Field field, Method method ) {
		
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
					return TOKEN_ERROR_EXCEPTION;
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
					return TOKEN_ERROR_EXCEPTION;
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
				return TOKEN_ERROR_EXCEPTION;
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
				return TOKEN_ERROR_EXCEPTION;
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
				return TOKEN_ERROR_EXCEPTION;
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
				return TOKEN_ERROR_EXCEPTION;
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
				return TOKEN_ERROR_EXCEPTION;
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
				return TOKEN_ERROR_EXCEPTION;
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
				return TOKEN_ERROR_EXCEPTION;
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
				return TOKEN_ERROR_EXCEPTION;
			}
		}
		else {
			if( strictPolicyEnable == true )
				return TOKEN_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
		}
		
		return 0;
	}
	
	private int stringToObjectProperties( char[] jsonCharArray, Object obj, Field fld ) {
		
		Class				clazz ;
		Field[]				fields ;
		String				fieldName ;
		Map<String,Field>	stringMapField ;
		Method				method ;
		Map<String,Method>	stringMapMethod ;
		int					nameBeginOffset ;
		int					nameEndOffset ;
		String				name ;
		TokenType			valueTokenType ;
		int					valueBeginOffset ;
		int					valueEndOffset ;
		
		int					nret ;
		
		clazz = obj.getClass();
		fields = clazz.getDeclaredFields() ;
		stringMapField = new HashMap<String,Field>() ;
		stringMapMethod = new HashMap<String,Method>() ;
		for( Field field : fields ) {
			fieldName = field.getName();
			stringMapField.put(fieldName, field);
			
			try {
				method = clazz.getDeclaredMethod( "set" + fieldName.substring(0,1).toUpperCase(Locale.getDefault()) + fieldName.substring(1), field.getType() ) ;
				stringMapMethod.put(fieldName, method);
			} catch (NoSuchMethodException e) {
				;
			} catch (SecurityException e) {
				;
			}
		}
		
		while(true) {
			// token "name"
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == TOKEN_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			nameBeginOffset = beginOffset ;
			nameEndOffset = endOffset ;
			
			name = new String(jsonCharArray,nameBeginOffset,nameEndOffset-nameBeginOffset+1) ;
			
			Field field = stringMapField.get(name) ;
			if( field == null ) {
				if( strictPolicyEnable == true )
					return TOKEN_ERROR_NAME_NOT_FOUND_IN_OBJECT;
			}

			method = stringMapMethod.get(name) ;
			
			if( tokenType != TokenType.TOKEN_TYPE_STRING ) {
				errorDesc = "name[" + String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1) + "] is not a string " ;
				return TOKEN_ERROR_NAME_INVALID;
			}
			
			// token ':' or ',' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == TOKEN_ERROR_END_OF_BUFFER ) {
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
				return TOKEN_ERROR_EXPECT_COLON_AFTER_NAME;
			}
			
			// token '{' or '[' or "value"
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == TOKEN_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			valueTokenType = tokenType ;
			valueBeginOffset = beginOffset ;
			valueEndOffset = endOffset ;
			
			if( tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE ) {
				try {
					Object inObj = field.getType().newInstance() ;
					if( inObj == null )
						return TOKEN_ERROR_UNEXPECT;
					nret = stringToObjectProperties( jsonCharArray, inObj, field ) ;
					if( nret != 0 )
						return nret;
					field.set( obj, inObj );
				} catch (Exception e) {
					e.printStackTrace();
					return TOKEN_ERROR_EXCEPTION;
				}
			}
			else if( tokenType == TokenType.TOKEN_TYPE_LEFT_BRACKET ) {
				try {
					Object inObj = field.getType().newInstance() ;
					if( inObj == null )
						return TOKEN_ERROR_UNEXPECT;
					nret = stringToListObject( jsonCharArray, inObj, field ) ;
					if( nret != 0 )
						return nret;
					field.set( obj, inObj );
				} catch (Exception e) {
					e.printStackTrace();
					return TOKEN_ERROR_EXCEPTION;
				}
			}
			else {
				nret = evalObjectProperty( jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset, obj, field, method ) ;
				if( nret != 0 )
					return nret;
			}
			
			// token ',' or '}' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == TOKEN_ERROR_END_OF_BUFFER ) {
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
				return TOKEN_ERROR_EXPECT_COLON_AFTER_NAME;
			}
		}
		
		return 0;
	}
	
	public <T> T stringToObject( char[] jsonCharArray, Class<T> clazz ) {
		
		T		obj ;
		
		jsonOffset = 0 ;
		jsonLength = jsonCharArray.length ;
		
		errorCode = tokenJsonWord( jsonCharArray ) ;
		if( errorCode != 0 ) {
			return null;
		}
		if( tokenType != TokenType.TOKEN_TYPE_LEFT_BRACE ) {
			errorCode = TOKEN_ERROR_FIND_FIRST_LEFT_BRACE ;
			return null;
		}
		
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		errorCode = stringToObjectProperties( jsonCharArray, obj, null ) ;
		if( errorCode != 0 )
			return null;
		
		return obj;
	}
	
	public <T> T stringToObject( String jsonString, Class<T> clazz ) {
		return stringToObject( jsonString.toCharArray(), clazz );
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
