package xyz.calvinwilliams.okjson;

import java.util.*;
import java.util.Map.Entry;
import java.lang.reflect.*;

public class OkJson {

	enum TokenType {
		TOKEN_TYPE_LEFT_BRACE , // {
		TOKEN_TYPE_RIGHT_BRACE , // }
		TOKEN_TYPE_LEFT_BRACKET, // [
		TOKEN_TYPE_RIGHT_BRACKET, // ]
		TOKEN_TYPE_COLON, // :
		TOKEN_TYPE_COMMA, // ,
		TOKEN_TYPE_STRING, // "ABC"
		TOKEN_TYPE_INTEGER, // 123
		TOKEN_TYPE_DECIMAL, // 123.456
		TOKEN_TYPE_BOOL, // true or false
		TOKEN_TYPE_NULL // null
	}
	
	private static ThreadLocal<HashMap<String,HashMap<String,Field>>>		stringMapFieldsCache ;
	private static ThreadLocal<HashMap<String,LinkedList<Field>>>			fieldsListCache ;
	private static ThreadLocal<HashMap<String,HashMap<String,Method>>>		stringMapMethodsCache ;
	private static ThreadLocal<StringBuilder>								jsonStringBufferCache ;
	private static ThreadLocal<StringBuilder>								fieldStringBufferCache ;
	
	private boolean				strictPolicyEnable ;
	private boolean				directAccessPropertyEnable ;
	private boolean				prettyFormatEnable ;
	
	private int					jsonOffset ;
	private int					jsonLength ;
	
	private TokenType			tokenType ;
	private int					beginOffset ;
	private int					endOffset ;
	private boolean				booleanValue ;
	
	private int					errorCode ;
	private String				errorDesc ;
	
	final private static int	OKJSON_ERROR_END_OF_BUFFER = 1 ;
	final private static int	OKJSON_ERROR_UNEXPECT = -4 ;
	final private static int	OKJSON_ERROR_EXCEPTION = -8 ;
	final private static int	OKJSON_ERROR_INVALID_BYTE = -11 ;
	final private static int	OKJSON_ERROR_FIND_FIRST_LEFT_BRACE = -21 ;
	final private static int	OKJSON_ERROR_NAME_INVALID = -22 ;
	final private static int	OKJSON_ERROR_EXPECT_COLON_AFTER_NAME = -23 ;
	final private static int	OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE = -24 ;
	final private static int	OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT = -26 ;
	final private static int	OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT = -28 ;
	final private static int	OKJSON_ERROR_NEW_OBJECT = -31 ;
	
	final private static String	TABS = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" ;
	
	final private static String	STRING_AFTER_ARRAY_PRETTYFORMAT = "] ,\n" ;
	final private static String	STRING_AFTER_ARRAY_PRETTYFORMAT_ENDLINE = "]\n" ;
	final private static String	STRING_AFTER_ARRAY = "]," ;
	final private static String	STRING_AFTER_ARRAY_ENDLINE = "]," ;
	
	final private static String	STRING_AFTER_OBJECT_PRETTYFORMAT = "} ,\n" ;
	final private static String	STRING_AFTER_OBJECT_PRETTYFORMAT_ENDLINE = "}\n" ;
	final private static String	STRING_AFTER_OBJECT = "}," ;
	final private static String	STRING_AFTER_OBJECT_ENDLINE = "}" ;
	
	private int tokenJsonString( char[] jsonCharArray ) {
		
		StringBuilder	fieldStringBuffer ;
		char			ch ;
		
		fieldStringBuffer = fieldStringBufferCache.get();
		fieldStringBuffer.delete( 0, fieldStringBuffer.length() );
		
		jsonOffset++;
		beginOffset = jsonOffset ;
		while( jsonOffset < jsonLength ) {
			ch = jsonCharArray[jsonOffset] ;
			if( ch == '"' ) {
				tokenType = TokenType.TOKEN_TYPE_STRING ;
				if( fieldStringBuffer.length() > 0 ) {
					if( jsonOffset > beginOffset ) {
						if( fieldStringBuffer.length() > 0 )
							fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
					}
				} else {
					endOffset = jsonOffset-1 ;
				}
				jsonOffset++;
				return 0;
			} else if ( ch == '\\' ) {
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == '"' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( '"' );
				} else if( ch == '\\' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( '\\' );
				} else if( ch == '/' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( "\\/" );
				} else if( ch == 'b' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( '\b' );
				} else if( ch == 'f' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( '\f' );
				} else if( ch == 'n' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( '\n' );
				} else if( ch == 'r' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( '\r' );
				} else if( ch == 't' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( '\t' );
				} else if( ch == 'u' ) {
					if( fieldStringBuffer.length() == 0 )
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					jsonOffset++;
					if( jsonOffset >= jsonLength ) {
						return OKJSON_ERROR_END_OF_BUFFER;
					}
					ch = jsonCharArray[jsonOffset] ;
					if( ('0'<=ch && ch<='9') || ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
						jsonOffset++;
						if( jsonOffset >= jsonLength ) {
							return OKJSON_ERROR_END_OF_BUFFER;
						}
						ch = jsonCharArray[jsonOffset] ;
						if( ('0'<=ch && ch<='9') || ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
							jsonOffset++;
							if( jsonOffset >= jsonLength ) {
								return OKJSON_ERROR_END_OF_BUFFER;
							}
							ch = jsonCharArray[jsonOffset] ;
							if( ('0'<=ch && ch<='9') || ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
								jsonOffset++;
								if( jsonOffset >= jsonLength ) {
									return OKJSON_ERROR_END_OF_BUFFER;
								}
								ch = jsonCharArray[jsonOffset] ;
								if( ('0'<=ch && ch<='9') || ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
									String unicodeString = "0x" + jsonCharArray[jsonOffset-3] + jsonCharArray[jsonOffset-2] + jsonCharArray[jsonOffset-1] + jsonCharArray[jsonOffset] ;
									int unicodeInt = Integer.decode(unicodeString).intValue() ;
									if( fieldStringBuffer.length() == 0 )
										fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-4-beginOffset-1 );
									fieldStringBuffer.append( (char)unicodeInt );
									beginOffset = jsonOffset + 1 ;
								} else {
									fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
									beginOffset = jsonOffset ;
									jsonOffset--;
								}
							} else {
								fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
								beginOffset = jsonOffset ;
								jsonOffset--;
							}
						} else {
							fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
							beginOffset = jsonOffset ;
							jsonOffset--;
						}
					} else {
						fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
						beginOffset = jsonOffset ;
						jsonOffset--;
					}
				} else {
					fieldStringBuffer.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuffer.append( ch );
				}
			}
			
			jsonOffset++;
		}
		
		return OKJSON_ERROR_END_OF_BUFFER;
	}
	
	private int tokenJsonNumber( char[] jsonCharArray ) {
		char	ch ;
		boolean	decimalPointFlag ;
		
		beginOffset = jsonOffset ;
		
		ch = jsonCharArray[jsonOffset] ;
		if( ch == '-' ) {
			jsonOffset++;
		}
		
		decimalPointFlag = false ;
		while( jsonOffset < jsonLength ) {
			ch = jsonCharArray[jsonOffset] ;
			if( '0' <= ch && ch <= '9' ) {
				jsonOffset++;
			} else if( ch == '.' ) {
				decimalPointFlag = true ;
				jsonOffset++;
			} else if( ch == 'e' || ch == 'E' ) {
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == '-' || ch == '+' ) {
					jsonOffset++;
				} else if( '0' <= ch && ch <= '9' ) {
					jsonOffset++;
				}
			} else {
				if( decimalPointFlag == true )
					tokenType = TokenType.TOKEN_TYPE_DECIMAL ;
				else
					tokenType = TokenType.TOKEN_TYPE_INTEGER ;
				endOffset = jsonOffset-1 ;
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
			if( ch == ' ' || ch == '\b' || ch == '\t' || ch == '\f' || ch == '\r' || ch == '\n' ) {
				jsonOffset++;
			} else if( ch == '{' ) {
				tokenType = TokenType.TOKEN_TYPE_LEFT_BRACE ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == '}'  ) {
				tokenType = TokenType.TOKEN_TYPE_RIGHT_BRACE ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == '['  ) {
				tokenType = TokenType.TOKEN_TYPE_LEFT_BRACKET ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == ']'  ) {
				tokenType = TokenType.TOKEN_TYPE_RIGHT_BRACKET ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == '"'  ) {
				return tokenJsonString( jsonCharArray ) ;
			} else if( ch == ':'  ) {
				tokenType = TokenType.TOKEN_TYPE_COLON ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == ','  ) {
				tokenType = TokenType.TOKEN_TYPE_COMMA ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == '-' || ( '0' <= ch && ch <= '9' ) ) {
				return tokenJsonNumber( jsonCharArray ) ;
			} else if( ch == 't' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'r' ) {
					jsonOffset++;
					if( jsonOffset >= jsonLength ) {
						return OKJSON_ERROR_END_OF_BUFFER;
					}
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'u' ) {
						jsonOffset++;
						if( jsonOffset >= jsonLength ) {
							return OKJSON_ERROR_END_OF_BUFFER;
						}
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
			} else if( ch == 'f' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'a' ) {
					jsonOffset++;
					if( jsonOffset >= jsonLength ) {
						return OKJSON_ERROR_END_OF_BUFFER;
					}
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'l' ) {
						jsonOffset++;
						if( jsonOffset >= jsonLength ) {
							return OKJSON_ERROR_END_OF_BUFFER;
						}
						ch = jsonCharArray[jsonOffset] ;
						if( ch == 's' ) {
							jsonOffset++;
							if( jsonOffset >= jsonLength ) {
								return OKJSON_ERROR_END_OF_BUFFER;
							}
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
			} else if( ch == 'n' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'u' ) {
					jsonOffset++;
					if( jsonOffset >= jsonLength ) {
						return OKJSON_ERROR_END_OF_BUFFER;
					}
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'l' ) {
						jsonOffset++;
						if( jsonOffset >= jsonLength ) {
							return OKJSON_ERROR_END_OF_BUFFER;
						}
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
			} else {
				errorDesc = "Invalid byte '" + ch + "'" ;
				return OKJSON_ERROR_INVALID_BYTE;
			}
		}
		
		return OKJSON_ERROR_END_OF_BUFFER;
	}
	
	private int addArrayObject( char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object object, Field field ) {

		try {
			Class<?> clazz = field.getType() ;
			if( clazz == ArrayList.class || clazz == LinkedList.class ) {
				Type type = field.getGenericType() ;
				ParameterizedType pt = (ParameterizedType) type ;
				Class<?> typeClass = (Class<?>) pt.getActualTypeArguments()[0] ;
				if( typeClass == String.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_STRING ) {
						String value = new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Byte.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Byte value = Byte.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Short.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Short value = Short.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Integer.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Integer value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Long.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Long value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Float.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
						Float value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Double.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
						Double value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Boolean.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
						((List<Object>) object).add( booleanValue );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
					;
				} else {
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

	private int stringToArrayObject( char[] jsonCharArray, Object object, Field field ) {
		
		TokenType			valueTokenType ;
		int					valueBeginOffset ;
		int					valueEndOffset ;
		
		int					nret ;
		
		while(true) {
			// token "value" or '{'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE ) {
				try {
					if( field != null ) {
						Class<?> clazz = field.getType() ;
						if( clazz == ArrayList.class || clazz == LinkedList.class ) {
							Type type = field.getGenericType() ;
							ParameterizedType pt = (ParameterizedType) type ;
							Class<?> typeClazz = (Class<?>) pt.getActualTypeArguments()[0] ;
							Object childObject = typeClazz.newInstance() ;
							nret = stringToObjectProperties( jsonCharArray , childObject ) ;
							if( nret != 0 )
								return nret;
							
							((List<Object>) object).add( childObject );
						}
					} else {
						nret = stringToObjectProperties( jsonCharArray , null ) ;
						if( nret != 0 )
							return nret;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( tokenType == TokenType.TOKEN_TYPE_STRING || tokenType == TokenType.TOKEN_TYPE_INTEGER || tokenType == TokenType.TOKEN_TYPE_DECIMAL || tokenType == TokenType.TOKEN_TYPE_BOOL ) {
				;
			} else {
				int beginPos = endOffset - 16 ;
				if( beginPos < 0 )
					beginPos = 0 ;
				errorDesc = "unexpect \""+String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1)+"\"" ;
				return OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE;
			}
			
			valueTokenType = tokenType ;
			valueBeginOffset = beginOffset ;
			valueEndOffset = endOffset ;
			
			// token ',' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_COMMA || tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				if( object != null && field != null ) {
					errorCode = addArrayObject( jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset , object, field ) ;
					if( errorCode != 0 )
						return errorCode;
				}
				
				if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET )
					break;
			} else {
				errorDesc = "unexpect \""+String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1)+"\"" ;
				return OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE;
			}
		}
		
		return 0;
	}
		
	private int setObjectProperty( char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object object, Field field, Method method ) {
		
		StringBuilder	fieldStringBuffer ;

		fieldStringBuffer = fieldStringBufferCache.get();
		
		if( field.getType() == String.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_STRING ) {
				try {
					String value ;
					if( fieldStringBuffer.length() > 0 ) {
						value = fieldStringBuffer.toString() ;
					} else {
						value = new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1) ;
					}
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.invoke(object, null);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Byte.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
				try {
					Byte	value = Byte.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.invoke(object, null);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Short.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
				try {
					Short	value = Short.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.invoke(object, null);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Integer.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
				try {
					Integer	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.invoke(object, null);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Long.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
				try {
					Long	value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.invoke(object, null);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Float.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
				try {
					Float	value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.invoke(object, null);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Double.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
				try {
					Double	value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.invoke(object, null);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Boolean.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
				try {
					Boolean	value = Boolean.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
				try {
					if( method != null ) {
						method.invoke(object, null);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, null );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType().getName().equals("byte") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				byte	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).byteValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setByte( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("short") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				short	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).shortValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setShort( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("int") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				int	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).intValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setInt( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("long") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				long	value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).longValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setLong( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("float") && valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
			try {
				float	value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).floatValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setFloat( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("double") && valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
			try {
				double	value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).doubleValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setDouble( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("boolean") && valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
			try {
				if( method != null ) {
					method.invoke(object, booleanValue);
				} else if( directAccessPropertyEnable == true ) {
					field.setBoolean( object, booleanValue );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
			try {
				if( method != null ) {
					method.invoke(object, null);
				} else if( directAccessPropertyEnable == true ) {
					field.set( object, null );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else {
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
		TokenType				fieldNameTokenType ;
		int						fieldNameBeginOffset ;
		int						fieldNameEndOffset ;
		String					fieldName ;
		TokenType				valueTokenType ;
		int						valueBeginOffset ;
		int						valueEndOffset ;
		
		int						nret ;
		
		if( object != null ) {
			clazz = object.getClass();
			
			stringMapFields = stringMapFieldsCache.get().get( clazz.getName() ) ;
			if( stringMapFields == null ) {
				stringMapFields = new HashMap<String,Field>() ;
				stringMapFieldsCache.get().put( clazz.getName(), stringMapFields ) ;
			}
			
			stringMapMethods = stringMapMethodsCache.get().get( clazz.getName() ) ;
			if( stringMapMethods == null ) {
				stringMapMethods = new HashMap<String,Method>() ;
				stringMapMethodsCache.get().put( clazz.getName(), stringMapMethods ) ;
			}
			
			if( stringMapFields.isEmpty() ) {
				fields = clazz.getDeclaredFields() ;
				for( Field f : fields ) {
	                f.setAccessible(true);
	                
					fieldName = f.getName();
					stringMapFields.put(fieldName, f);
					
					try {
						method = clazz.getDeclaredMethod( "set" + fieldName.substring(0,1).toUpperCase(Locale.getDefault()) + fieldName.substring(1), f.getType() ) ;
						method.setAccessible(true);
						stringMapMethods.put(fieldName, method);
					} catch (NoSuchMethodException e2) {
						;
					} catch (SecurityException e2) {
						;
					}
				}
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
			
			fieldNameTokenType = tokenType ;
			fieldNameBeginOffset = beginOffset ;
			fieldNameEndOffset = endOffset ;
			fieldName = new String(jsonCharArray,fieldNameBeginOffset,fieldNameEndOffset-fieldNameBeginOffset+1) ;
			
			if( object != null ) {
				field = stringMapFields.get(fieldName) ;
				if( field == null ) {
					if( strictPolicyEnable == true )
						return OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT;
				}
				
				method = stringMapMethods.get(fieldName) ;
			} else {
				field = null ;
				method = null ;
			}
			
			if( tokenType != TokenType.TOKEN_TYPE_STRING ) {
				errorDesc = "expect a name but \""+String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1)+"\"" ;
				return OKJSON_ERROR_NAME_INVALID;
			}
			
			// token ':' or ',' or '}' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_COLON ) {
				;
			} else if( tokenType == TokenType.TOKEN_TYPE_COMMA || tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE ) {
				clazz = field.getType() ;
				if( clazz == ArrayList.class || clazz == LinkedList.class ) {
					nret = addArrayObject( jsonCharArray, fieldNameTokenType, fieldNameBeginOffset, fieldNameEndOffset, object, field ) ;
					if( nret != 0 )
						return nret;
					
					if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE )
						break;
				}
			} else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				break;
			} else {
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
						nret = stringToArrayObject( jsonCharArray, childObject, field ) ;
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
			} else {
				if( object != null && field != null ) {
					nret = setObjectProperty( jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset, object, field, method ) ;
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
			} else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE ) {
				break;
			} else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				break;
			} else {
				errorDesc = "expect ',' or '}' or ']' but \"" + String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1) + "\"" ;
				return OKJSON_ERROR_EXPECT_COLON_AFTER_NAME;
			}
		}
		
		return 0;
	}
	
	public <T> T stringToObject( String jsonString, T object ) {
		
		char[]	jsonCharArray ;
		
		jsonCharArray = jsonString.toCharArray() ;
		jsonOffset = 0 ;
		jsonLength = jsonCharArray.length ;
		
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
		
		if( fieldStringBufferCache == null ) {
			fieldStringBufferCache = new ThreadLocal<StringBuilder>() ;
			if( fieldStringBufferCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldStringBufferCache.set(new StringBuilder(64));
		}
		
		errorCode = tokenJsonWord( jsonCharArray ) ;
		if( errorCode != 0 ) {
			return null;
		}
		
		if( tokenType != TokenType.TOKEN_TYPE_LEFT_BRACE ) {
			errorCode = OKJSON_ERROR_FIND_FIRST_LEFT_BRACE ;
			return null;
		}
		
		errorCode = stringToObjectProperties( jsonCharArray, object ) ;
		if( errorCode != 0 )
			return null;
		
		return object;
	}
	
	public <T> T stringToObject( String jsonString, Class<T> clazz ) {
		
		T		object ;
		
		try {
			object = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return stringToObject( jsonString, object );
	}
	
	private void appendBuilderTabs( StringBuilder jsonStringBuffer, int depth ) {
		
		if( depth <= TABS.length() ) {
			jsonStringBuffer.append( TABS, 0, depth );
		} else {
			for( int i = 1 ; i < depth ; i++ ) {
				jsonStringBuffer.append( '\t' );
			}
		}
		
		return;
	}
	
	private int objectToListString( List<Object> array, int arrayCount, Field field, StringBuilder jsonStringBuffer, StringBuilder fieldStringBuffer, int depth ) {
		
		int				arrayIndex ;
		int				nret ;
		
		try {
				Type type = field.getGenericType() ;
				ParameterizedType pt = (ParameterizedType) type ;
				Class<?> typeClazz = (Class<?>) pt.getActualTypeArguments()[0] ;
				if(		typeClazz == String.class
						|| typeClazz == Byte.class || typeClazz == Short.class || typeClazz == Integer.class || typeClazz == Long.class
						|| typeClazz == Float.class || typeClazz == Double.class
						|| typeClazz == Boolean.class ) {
					
						if( prettyFormatEnable ) {
							appendBuilderTabs( jsonStringBuffer, depth+1 );
						}
						
						arrayIndex = 0 ;
						for( Object object : array ) {
							arrayIndex++;
							if( prettyFormatEnable ) {
								if( arrayIndex < arrayCount ) {
									jsonStringBuffer.append( object+" , " );
								} else {
									jsonStringBuffer.append( object );
								}
							} else {
								if( arrayIndex < arrayCount ) {
									jsonStringBuffer.append( object+"," );
								} else {
									jsonStringBuffer.append( object );
								}
							}
						}
						
						if( prettyFormatEnable )
							jsonStringBuffer.append( "\n" );
				} else {
					arrayIndex = 0 ;
					for( Object object : array ) {
						arrayIndex++;
						if( object != null ) {
							if( prettyFormatEnable ) {
								appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( "{\n" );
							} else {
								jsonStringBuffer.append( "{" );
							}
							
							nret = objectToPropertiesString( object, jsonStringBuffer, fieldStringBuffer, depth+1 ) ;
							if( nret != 0 )
								return nret;
							
							if( prettyFormatEnable ) {
								appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( "}" );
								if( arrayIndex < arrayCount )
									jsonStringBuffer.append( " ," );
								jsonStringBuffer.append( '\n' );
							} else {
								jsonStringBuffer.append( "}" );
							}
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			return OKJSON_ERROR_EXCEPTION;
		}
		
		return 0;
	}
	
	private String unfoldEscape( String value, StringBuilder fieldStringBuffer ) {
		
		char[]			jsonCharArray = value.toCharArray() ;
		int				jsonCharArrayLength = value.length() ;
		int				jsonCharArrayIndex ;
		int				segmentBeginOffset ;
		int				segmentLen ;
		char			ch ;
		
		fieldStringBuffer.setLength(0);
		
		segmentBeginOffset = 0 ;
		for( jsonCharArrayIndex = 0 ; jsonCharArrayIndex < jsonCharArrayLength ; jsonCharArrayIndex++ ) {
			ch = jsonCharArray[jsonCharArrayIndex] ;
			if( ch == '\"' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
				fieldStringBuffer.append( "\\\"" ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if( ch == '\\' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
				fieldStringBuffer.append( "\\\\" ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if( ch == '/' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
				fieldStringBuffer.append( "\\/" ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( ch == '\t' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
				fieldStringBuffer.append( "\\t" ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( ch == '\f' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
				fieldStringBuffer.append( "\\f" ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( ch == '\b' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
				fieldStringBuffer.append( "\\b" ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( ch == '\n' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
				fieldStringBuffer.append( "\\n" ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( ch == '\r' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
				fieldStringBuffer.append( "\\r" ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			}
		}
		if( fieldStringBuffer.length() > 0 && segmentBeginOffset < jsonCharArrayIndex ) {
			segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
			if( segmentLen > 0 )
				fieldStringBuffer.append( jsonCharArray, segmentBeginOffset, segmentLen );
		}
		
		if( fieldStringBuffer.length() == 0 )
			return value;
		else
			return fieldStringBuffer.toString();
	}
	
	private int objectToPropertiesString( Object object, StringBuilder jsonStringBuffer, StringBuilder fieldStringBuffer, int depth ) {
		
		Class<?>				clazz ;
		LinkedList<Field>		fieldsList ;
		HashMap<String,Method>	stringMapMethods ;
		Field[]					fields ;
		String					methodName ;
		Method					method ;
		String					fieldName ;
		byte[]					fieldName2 ;
		int						fieldIndex ;
		int						fieldCount ;
		
		int						nret = 0 ;
		
		clazz = object.getClass();
		
		fieldsList = fieldsListCache.get().get( clazz.getName() ) ;
		if( fieldsList == null ) {
			fieldsList = new LinkedList<Field>() ;
			fieldsListCache.get().put( clazz.getName(), fieldsList ) ;
		}
		
		stringMapMethods = stringMapMethodsCache.get().get( clazz.getName() ) ;
		if( stringMapMethods == null ) {
			stringMapMethods = new HashMap<String,Method>() ;
			stringMapMethodsCache.get().put( clazz.getName(), stringMapMethods ) ;
		}
		
		if( fieldsList.isEmpty() ) {
			fields = clazz.getDeclaredFields() ;
			for( Field f : fields ) {
				f.setAccessible(true);
				fieldName = f.getName();
				fieldsList.add(f);
				
				try {
					method = stringMapMethods.get(fieldName) ;
					if( method == null ) {
						if( f.getType() == Boolean.class || f.getType().getName().equals("boolean") ) {
							methodName = "is" + fieldName.substring(0,1).toUpperCase(Locale.getDefault()) + fieldName.substring(1) ;
						} else {
							methodName = "get" + fieldName.substring(0,1).toUpperCase(Locale.getDefault()) + fieldName.substring(1) ;
						}
						method = clazz.getDeclaredMethod( methodName ) ;
						method.setAccessible(true);
						stringMapMethods.put(fieldName, method);
					}
				} catch (NoSuchMethodException e) {
					;
				} catch (Exception e) {
					return OKJSON_ERROR_UNEXPECT;
				}
			}
		}
		
		fieldIndex = 0 ;
		fieldCount = fieldsList.size() ;
		for( Field field : fieldsList ) {
			fieldIndex++;
			
			fieldName = field.getName() ;
			
			try {
				method = stringMapMethods.get(fieldName) ;
				if( method == null ) {
					if( field.getType() == Boolean.class || field.getType().getName().equals("boolean") ) {
						methodName = "is" + fieldName.substring(0,1).toUpperCase(Locale.getDefault()) + fieldName.substring(1) ;
					} else {
						methodName = "get" + fieldName.substring(0,1).toUpperCase(Locale.getDefault()) + fieldName.substring(1) ;
					}
					method = clazz.getDeclaredMethod( methodName ) ;
					method.setAccessible(true);
					stringMapMethods.put(fieldName, method);
				}
				
				if( field.getType() == String.class
						|| field.getType() == Byte.class || field.getType() == Short.class || field.getType() == Integer.class || field.getType() == Long.class
						|| field.getType() == Float.class || field.getType() == Double.class
						|| field.getType() == Boolean.class
						|| field.getType().isPrimitive() ) {
					Object value = method.invoke( object ) ;
					
					if( prettyFormatEnable ) {
						if( field.getType() == String.class && value != null ) {
							String fieldValue = unfoldEscape( (String)value, fieldStringBuffer ) ;
							appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( " : \"" ).append( fieldValue ).append( "\"" );
						} else {
							appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( " : " ).append( value );
						}
						if( fieldIndex < fieldCount ) {
							jsonStringBuffer.append( " ," );
						}
						jsonStringBuffer.append( '\n' );
					} else {
//						if( field.getType() == String.class && value != null ) {
//							String fieldValue = unfoldEscape( (String)value, fieldStringBuffer ) ;
//							jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( ":\"" ).append( fieldValue ).append( "\"" );
//						} else {
//							jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( ":" ).append( value );
//						}
//						if( fieldIndex < fieldCount )
//							jsonStringBuffer.append( ',' );
					}
				} else if ( field.getType() == ArrayList.class || field.getType() == LinkedList.class ) {
					try {
						List<Object> array = (List<Object>)field.get(object);
						if( array != null ) {
							int arrayCount = array.size() ;
							if( arrayCount > 0 ) {
								if( prettyFormatEnable ) {
									appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( " : [\n");
								} else {
//									jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( ':' ).append( '"' ).append( fieldName ).append( '"' ).append( "[" );
								}
								
								nret = objectToListString( array, arrayCount, field, jsonStringBuffer, fieldStringBuffer, depth+1 ) ;
								if( nret != 0 )
									return nret;
								
								if( prettyFormatEnable ) {
									appendBuilderTabs( jsonStringBuffer, depth+1 );
									if( fieldIndex < fieldCount ) {
										jsonStringBuffer.append( STRING_AFTER_ARRAY_PRETTYFORMAT );
									} else {
										jsonStringBuffer.append( STRING_AFTER_ARRAY_PRETTYFORMAT_ENDLINE );
									}
								} else {
//									if( fieldIndex < fieldCount ) {
//										jsonStringBuffer.append( STRING_AFTER_ARRAY );
//									} else {
//										jsonStringBuffer.append( STRING_AFTER_ARRAY_ENDLINE );
//									}
								}
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						return OKJSON_ERROR_UNEXPECT;
					}
				} else {
					if( prettyFormatEnable ) {
						appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( " : {\n");
					} else {
//						jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( ":{" );
					}
					
					Object value = method.invoke( object ) ;
					nret = objectToPropertiesString( value, jsonStringBuffer, fieldStringBuffer, depth+1 ) ;
					if( nret != 0 )
						return nret;
					
					if( prettyFormatEnable ) {
						appendBuilderTabs( jsonStringBuffer, depth+1 );
						if( fieldIndex < fieldCount ) {
							jsonStringBuffer.append( STRING_AFTER_OBJECT_PRETTYFORMAT );
						} else {
							jsonStringBuffer.append( STRING_AFTER_OBJECT_PRETTYFORMAT_ENDLINE );
						}
					} else {
//						if( fieldIndex < fieldCount ) {
//							jsonStringBuffer.append( STRING_AFTER_OBJECT );
//						} else {
//							jsonStringBuffer.append( STRING_AFTER_OBJECT_ENDLINE );
//						}
					}
				}
			} catch (NoSuchMethodException e) {
				if( directAccessPropertyEnable == true ) {
					if( field.getType() == String.class
							|| field.getType() == Byte.class || field.getType() == Short.class || field.getType() == Integer.class || field.getType() == Long.class
							|| field.getType() == Float.class || field.getType() == Double.class
							|| field.getType() == Boolean.class
							|| field.getType().isPrimitive() ) {
						try {
							Object value = field.get( object );
							if( prettyFormatEnable ) {
								if( field.getType() == String.class && value != null ) {
									String fieldValue = unfoldEscape( (String)value, fieldStringBuffer ) ;
									appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( " : \"" ).append( fieldValue ).append( "\"" );
								} else {
									appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( " : " ).append( value );
								}
								if( fieldIndex < fieldCount ) {
									jsonStringBuffer.append( " ," );
								}
								jsonStringBuffer.append( '\n' );
							} else {
								if( field.getType() == String.class && value != null ) {
									String fieldValue = unfoldEscape( (String)value, fieldStringBuffer ) ;
									jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( ":\"" ).append( fieldValue ).append( "\"" );
								} else {
									jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( ":" ).append( value );
								}
								if( fieldIndex < fieldCount )
									jsonStringBuffer.append( ',' );
							}
						} catch (Exception e2) {
							e.printStackTrace();
							return OKJSON_ERROR_UNEXPECT;
						}
					} else if ( field.getType() == ArrayList.class || field.getType() == LinkedList.class ) {
						try {
							List<Object> array = (List<Object>)field.get(object);
							if( array != null ) {
								int arrayCount = array.size() ;
								if( arrayCount > 0 ) {
									if( prettyFormatEnable ) {
										appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( " : [\n");
									} else {
										jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( ":[");
									}
									
									nret = objectToListString( array, arrayCount, field, jsonStringBuffer, fieldStringBuffer, depth+1 ) ;
									if( nret != 0 )
										return nret;

									if( prettyFormatEnable ) {
										appendBuilderTabs( jsonStringBuffer, depth+1 );
										if( fieldIndex < fieldCount ) {
											jsonStringBuffer.append( STRING_AFTER_ARRAY_PRETTYFORMAT );
										} else {
											jsonStringBuffer.append( STRING_AFTER_ARRAY_PRETTYFORMAT_ENDLINE );
										}
									} else {
										if( fieldIndex < fieldCount ) {
											jsonStringBuffer.append( STRING_AFTER_ARRAY );
										} else {
											jsonStringBuffer.append( STRING_AFTER_ARRAY_ENDLINE );
										}
									}
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
							return OKJSON_ERROR_UNEXPECT;
						}
					} else {
						try {
							Object value = field.get( object );
							if( value != null  ) {
								if( prettyFormatEnable ) {
									appendBuilderTabs( jsonStringBuffer, depth+1 ); jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( " : {\n");
								} else {
									jsonStringBuffer.append( '"' ).append( fieldName ).append( '"' ).append( ":{");
								}
								
								nret = objectToPropertiesString( value, jsonStringBuffer, fieldStringBuffer, depth+1 ) ;
								if( nret != 0 )
									return nret;
								
								if( prettyFormatEnable ) {
									appendBuilderTabs( jsonStringBuffer, depth+1 );
									if( fieldIndex < fieldCount ) {
										jsonStringBuffer.append( STRING_AFTER_OBJECT_PRETTYFORMAT );
									} else {
										jsonStringBuffer.append( STRING_AFTER_OBJECT_PRETTYFORMAT_ENDLINE );
									}
								} else {
									if( fieldIndex < fieldCount ) {
										jsonStringBuffer.append( STRING_AFTER_OBJECT );
									} else {
										jsonStringBuffer.append( STRING_AFTER_OBJECT_ENDLINE );
									}
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
							return OKJSON_ERROR_UNEXPECT;
						}
					}
					
					
				}
			} catch (Exception e) {
				return OKJSON_ERROR_UNEXPECT;
			}
		}
		
		return 0;
	}
	
	public String objectToString( Object object ) {
		
		StringBuilder	jsonStringBuffer ;
		StringBuilder	fieldStringBuffer ;
		int				nret = 0 ;
		
		if( fieldsListCache == null ) {
			fieldsListCache = new ThreadLocal<HashMap<String,LinkedList<Field>>>() ;
			if( fieldsListCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldsListCache.set(new HashMap<String,LinkedList<Field>>());
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
		
		if( jsonStringBufferCache == null ) {
			jsonStringBufferCache = new ThreadLocal<StringBuilder>() ;
			if( jsonStringBufferCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			jsonStringBuffer = new StringBuilder(1024) ;
			if( jsonStringBuffer == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			jsonStringBufferCache.set(jsonStringBuffer);
		} else {
			jsonStringBuffer = jsonStringBufferCache.get() ;
		}
		jsonStringBuffer.setLength(0);
		
		if( fieldStringBufferCache == null ) {
			fieldStringBufferCache = new ThreadLocal<StringBuilder>() ;
			if( fieldStringBufferCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldStringBuffer = new StringBuilder(1024) ;
			if( fieldStringBuffer == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldStringBufferCache.set(fieldStringBuffer);
		} else {
			fieldStringBuffer = fieldStringBufferCache.get();
		}
		
		if( prettyFormatEnable ) {
			jsonStringBuffer.append( "{\n" );
		} else {
//			jsonStringBuffer.append( "{" );
		}
		
		errorCode = objectToPropertiesString( object, jsonStringBuffer, fieldStringBuffer, 1 );
		if( errorCode != 0 )
			return null;
		
		if( prettyFormatEnable ) {
			jsonStringBuffer.append( "}\n" );
		} else {
//			jsonStringBuffer.append( "}" );
		}
		
		return jsonStringBuffer.toString();
	}
	
	public void setDirectAccessPropertyEnable( boolean b ) {
		this.directAccessPropertyEnable = b ;
	}
	
	public void setStrictPolicy( boolean b ) {
		this.strictPolicyEnable = b ;
	}
	
	public void setPrettyFormatEnable( boolean b ) {
		this.prettyFormatEnable = b;
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
