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
	
	enum ClassFieldType {
		CLASSFIELDTYPE_STRING ,
		CLASSFIELDTYPE_NUMBER ,
		CLASSFIELDTYPE_LIST ,
		CLASSFIELDTYPE_SUBCLASS
	}
	
	class ClassField {
		Field			field ;
		ClassFieldType	classFieldType ;
	}
	
	private static ThreadLocal<HashMap<String,HashMap<String,Field>>>		stringMapFieldsCache ;
	private static ThreadLocal<HashMap<String,LinkedList<Field>>>			fieldsListCache ;
	private static ThreadLocal<HashMap<String,LinkedList<ClassFieldType>>>	classMapFieldTypeListCache ;
	private static ThreadLocal<HashMap<String,HashMap<String,Method>>>		stringMapMethodsCache ;
	private static ThreadLocal<StringBuilder>								jsonStringBuilderCache ;
	private static ThreadLocal<StringBuilder>								fieldStringBuilderCache ;
	private static ThreadLocal<OkJsonCharArrayBuilder>						jsonByteArrayBuilderCache ;
	private static ThreadLocal<OkJsonCharArrayBuilder>						fieldByteArrayBuilderCache ;
	private static ThreadLocal<HashMap<Class,Boolean>>						basicTypeClassMapBooleanCache ;
	
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
	
	final private static String	STRING_AFTER_ARRAY_PRETTYFORMAT = "] ,\n" ;
	final private static String	STRING_AFTER_ARRAY_PRETTYFORMAT_ENDLINE = "]\n" ;
	final private static String	STRING_AFTER_ARRAY = "]," ;
	final private static String	STRING_AFTER_ARRAY_ENDLINE = "]," ;
	
	final private static String	STRING_AFTER_OBJECT_PRETTYFORMAT = "} ,\n" ;
	final private static String	STRING_AFTER_OBJECT_PRETTYFORMAT_ENDLINE = "}\n" ;
	final private static String	STRING_AFTER_OBJECT = "}," ;
	final private static String	STRING_AFTER_OBJECT_ENDLINE = "}" ;
	
	private int tokenJsonString( char[] jsonCharArray ) {
		
		StringBuilder	fieldStringBuilder ;
		char					ch ;
		
		fieldStringBuilder = fieldStringBuilderCache.get();
		fieldStringBuilder.setLength(0);
		
		jsonOffset++;
		beginOffset = jsonOffset ;
		while( jsonOffset < jsonLength ) {
			ch = jsonCharArray[jsonOffset] ;
			if( ch == '"' ) {
				tokenType = TokenType.TOKEN_TYPE_STRING ;
				if( fieldStringBuilder.length() > 0 ) {
					if( jsonOffset > beginOffset ) {
						if( fieldStringBuilder.length() > 0 )
							fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
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
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '"' );
				} else if( ch == '\\' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\\' );
				} else if( ch == '/' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( "\\/" );
				} else if( ch == 'b' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\b' );
				} else if( ch == 'f' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\f' );
				} else if( ch == 'n' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\n' );
				} else if( ch == 'r' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\r' );
				} else if( ch == 't' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\t' );
				} else if( ch == 'u' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
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
									if( fieldStringBuilder.length() == 0 )
										fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-4-beginOffset-1 );
									fieldStringBuilder.append( (char)unicodeInt );
									beginOffset = jsonOffset + 1 ;
								} else {
									fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
									beginOffset = jsonOffset ;
									jsonOffset--;
								}
							} else {
								fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
								beginOffset = jsonOffset ;
								jsonOffset--;
							}
						} else {
							fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
							beginOffset = jsonOffset ;
							jsonOffset--;
						}
					} else {
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
						beginOffset = jsonOffset ;
						jsonOffset--;
					}
				} else {
					fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( ch );
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
		
		StringBuilder	fieldStringBuilder ;

		fieldStringBuilder = fieldStringBuilderCache.get();
		
		if( field.getType() == String.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_STRING ) {
				try {
					String value ;
					if( fieldStringBuilder.length() > 0 ) {
						value = fieldStringBuilder.toString() ;
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
		
		if( fieldStringBuilderCache == null ) {
			fieldStringBuilderCache = new ThreadLocal<StringBuilder>() ;
			if( fieldStringBuilderCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldStringBuilderCache.set(new StringBuilder(64));
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
	
	/* ------------------------------ blablabla ------------------------------ */
	
	private int objectToListString( List<Object> array, int arrayCount, Field field, OkJsonCharArrayBuilder jsonCharArrayBuilder, int depth ) {
		
		HashMap<Class,Boolean>	basicTypeClassMapBoolean = basicTypeClassMapBooleanCache.get();
		int						arrayIndex ;
		boolean					isNotLastLine ;
		int						nret ;
		
		try {
				Type type = field.getGenericType() ;
				ParameterizedType pt = (ParameterizedType) type ;
				Class<?> typeClazz = (Class<?>) pt.getActualTypeArguments()[0] ;
				Boolean b = basicTypeClassMapBoolean.get( typeClazz ) ;
				if( b == null )
					b = false ;
				if(	b ) {
						if( prettyFormatEnable ) {
							jsonCharArrayBuilder.appendTabs(depth+1);
						}
						
						arrayIndex = 0 ;
						for( Object object : array ) {
							arrayIndex++;
							if( arrayIndex < arrayCount )
								isNotLastLine = false ;
							else
								isNotLastLine = true ;
							if( prettyFormatEnable ) {
								if( field.getType() == String.class && object != null ) {
									String str = (String)object ;
									jsonCharArrayBuilder.appendJsonStringPretty(str.toCharArray(),isNotLastLine);
								} else {
									jsonCharArrayBuilder.appendJsonValuePretty(object.toString().toCharArray(),isNotLastLine);
								}
							} else {
								if( field.getType() == String.class && object != null ) {
									String str = (String)object ;
									jsonCharArrayBuilder.appendJsonString(str.toCharArray(),isNotLastLine);
								} else {
									jsonCharArrayBuilder.appendJsonValue(object.toString().toCharArray(),isNotLastLine);
								}
							}
						}
						
						if( prettyFormatEnable )
							jsonCharArrayBuilder.appendChar( '\n' );
				} else {
					arrayIndex = 0 ;
					for( Object object : array ) {
						arrayIndex++;
						if( arrayIndex < arrayCount )
							isNotLastLine = false ;
						else
							isNotLastLine = true ;
						if( object != null ) {
							if( prettyFormatEnable ) {
								jsonCharArrayBuilder.appendTabs(depth+1).appendCloseBytePretty('{',isNotLastLine);
							} else {
								jsonCharArrayBuilder.appendCloseByte('{',isNotLastLine);
							}
							
							nret = objectToPropertiesString( object, jsonCharArrayBuilder, depth+1 ) ;
							if( nret != 0 )
								return nret;
							
							if( prettyFormatEnable ) {
								jsonCharArrayBuilder.appendTabs(depth+1).appendCloseBytePretty('}',isNotLastLine);
							} else {
								jsonCharArrayBuilder.appendCloseByte('}',isNotLastLine);
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
	
	private String unfoldEscape( String value ) {
		
		OkJsonCharArrayBuilder	fieldCharArrayBuilder = fieldByteArrayBuilderCache.get() ;
		char[]					jsonCharArrayBuilder = value.toCharArray() ;
		int						jsonCharArrayLength = value.length() ;
		int						jsonCharArrayIndex ;
		int						segmentBeginOffset ;
		int						segmentLen ;
		char					c ;
		
		fieldCharArrayBuilder.setLength(0);
		
		segmentBeginOffset = 0 ;
		for( jsonCharArrayIndex = 0 ; jsonCharArrayIndex < jsonCharArrayLength ; jsonCharArrayIndex++ ) {
			c = jsonCharArrayBuilder[jsonCharArrayIndex] ;
			if( c == '\"' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\\"".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if( c == '\\' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\\\".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if( c == '/' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\/".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\t' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\t".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\f' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\f".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\b' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\b".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\n' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\n".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\r' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\r".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			}
		}
		if( fieldCharArrayBuilder.getLength() > 0 && segmentBeginOffset < jsonCharArrayIndex ) {
			segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
			if( segmentLen > 0 )
				fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
		}
		
		if( fieldCharArrayBuilder.getLength() == 0 )
			return value;
		else
			return fieldCharArrayBuilder.toString();
	}
	
	private int objectToPropertiesString( Object object, OkJsonCharArrayBuilder jsonCharArrayBuilder, int depth ) {
		
		HashMap<Class,Boolean>		basicTypeClassMapBoolean = basicTypeClassMapBooleanCache.get();
		Class<?>					clazz ;
		LinkedList<ClassFieldType>	classFieldTypeList ;
		HashMap<String,Method>		stringMapMethods ;
		Field[]						fields ;
		String						methodName ;
		Method						method ;
		String						fieldName ;
		char[]						fieldName2 ;
		int							fieldIndex ;
		int							fieldCount ;
		boolean						isNotLastLine ;
		
		int							nret = 0 ;
		
		clazz = object.getClass();
		
		classFieldTypeList = classMapFieldTypeListCache.get().get( clazz.getName() ) ;
		if( classFieldTypeList == null ) {
			classFieldTypeList = new LinkedList<ClassFieldType>() ;
			classMapFieldTypeListCache.get().put( clazz.getName(), ClassFieldTypeList ) ;
		}
		
		stringMapMethods = stringMapMethodsCache.get().get( clazz.getName() ) ;
		if( stringMapMethods == null ) {
			stringMapMethods = new HashMap<String,Method>() ;
			stringMapMethodsCache.get().put( clazz.getName(), stringMapMethods ) ;
		}
		
		if( classFieldTypeList.isEmpty() ) {
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
					e.printStackTrace();
					return OKJSON_ERROR_UNEXPECT;
				}
			}
		}
		
		fieldIndex = 0 ;
		fieldCount = fieldsList.size() ;
		for( Field field : fieldsList ) {
			fieldIndex++;
			if( fieldIndex < fieldCount )
				isNotLastLine = false ;
			else
				isNotLastLine = true ;
			
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
				
				Boolean b = basicTypeClassMapBoolean.get( field.getType() ) ;
				if( b == null )
					b = false ;
				if( b || field.getType().isPrimitive() ) {
					Object value = method.invoke( object ) ;
					
					if( prettyFormatEnable ) {
						if( field.getType() == String.class && value != null ) {
							String fieldValue = unfoldEscape( (String)value ) ;
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndStringPretty(fieldName.toCharArray(),fieldValue.toCharArray(),isNotLastLine);
						} else {
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndValuePretty(fieldName.toCharArray(),value.toString().toCharArray(),isNotLastLine);
						}
					} else {
						if( field.getType() == String.class && value != null ) {
							String fieldValue = unfoldEscape( (String)value ) ;
							jsonCharArrayBuilder.appendJsonNameAndColonAndStringPretty(fieldName.toCharArray(),fieldValue.toCharArray(),isNotLastLine);
						} else {
							jsonCharArrayBuilder.appendJsonNameAndColonAndStringPretty(fieldName.toCharArray(),value.toString().toCharArray(),isNotLastLine);
						}
					}
				} else if ( field.getType() == ArrayList.class || field.getType() == LinkedList.class ) {
					try {
						List<Object> array = (List<Object>)field.get(object);
						if( array != null ) {
							int arrayCount = array.size() ;
							if( arrayCount > 0 ) {
								if( prettyFormatEnable ) {
									jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndOpenBytePretty(fieldName.toCharArray(),'[');
								} else {
									jsonCharArrayBuilder.appendJsonNameAndColonAndOpenByte(fieldName.toCharArray(),'[');
								}
								
								nret = objectToListString( array, arrayCount, field, jsonCharArrayBuilder, depth+1 ) ;
								if( nret != 0 )
									return nret;
								
								if( prettyFormatEnable ) {
									jsonCharArrayBuilder.appendTabs(depth+1).appendCloseBytePretty(']',isNotLastLine);
								} else {
									jsonCharArrayBuilder.appendCloseByte(']',isNotLastLine);
								}
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						return OKJSON_ERROR_UNEXPECT;
					}
				} else {
					if( prettyFormatEnable ) {
						jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndOpenBytePretty(fieldName.toCharArray(),'{');
					} else {
						jsonCharArrayBuilder.appendJsonNameAndColonAndOpenByte(fieldName.toCharArray(),'{');
					}
					
					Object value = method.invoke( object ) ;
					nret = objectToPropertiesString( value, jsonCharArrayBuilder, depth+1 ) ;
					if( nret != 0 )
						return nret;
					
					if( prettyFormatEnable ) {
						jsonCharArrayBuilder.appendTabs(depth+1).appendCloseBytePretty('}',isNotLastLine);
					} else {
						jsonCharArrayBuilder.appendTabs(depth+1).appendCloseByte('}',isNotLastLine);
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
									String fieldValue = unfoldEscape( (String)value ) ;
									jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndStringPretty(fieldName.toCharArray(),fieldValue.toCharArray(),isNotLastLine);
								} else {
									jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndValuePretty(fieldName.toCharArray(),value.toString().toCharArray(),isNotLastLine);
								}
							} else {
								if( field.getType() == String.class && value != null ) {
									String fieldValue = unfoldEscape( (String)value ) ;
									jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndString(fieldName.toCharArray(),fieldValue.toCharArray(),isNotLastLine);
								} else {
									jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndValue(fieldName.toCharArray(),value.toString().toCharArray(),isNotLastLine);
								}
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
										jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndOpenBytePretty(fieldName.toCharArray(),'[');
									} else {
										jsonCharArrayBuilder.appendJsonNameAndColonAndOpenByte(fieldName.toCharArray(),'[');
									}
									
									nret = objectToListString( array, arrayCount, field, jsonCharArrayBuilder, depth+1 ) ;
									if( nret != 0 )
										return nret;

									if( prettyFormatEnable ) {
										jsonCharArrayBuilder.appendTabs(depth+1).appendCloseBytePretty(']',isNotLastLine);
									} else {
										jsonCharArrayBuilder.appendCloseByte(']',isNotLastLine);
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
									jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndOpenBytePretty(fieldName.toCharArray(),'{');
								} else {
									jsonCharArrayBuilder.appendJsonNameAndColonAndOpenByte(fieldName.toCharArray(),'{');
								}
								
								nret = objectToPropertiesString( value, jsonCharArrayBuilder, depth+1 ) ;
								if( nret != 0 )
									return nret;
								
								if( prettyFormatEnable ) {
									jsonCharArrayBuilder.appendTabs(depth+1).appendCloseBytePretty('}',isNotLastLine);
								} else {
									jsonCharArrayBuilder.appendTabs(depth+1).appendCloseByte('}',isNotLastLine);
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
							return OKJSON_ERROR_UNEXPECT;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_UNEXPECT;
			}
		}
		
		return 0;
	}
	
	public String objectToString( Object object ) {
		
		OkJsonCharArrayBuilder	jsonCharArrayBuilder ;
		OkJsonCharArrayBuilder	fieldCharArrayBuilder ;
		HashMap<Class,Boolean>	basicTypeClassMapString ;
		int						nret = 0 ;
		
		if( classMapFieldListCache == null ) {
			classMapFieldListCache = new ThreadLocal<HashMap<String,LinkedList<ClassFieldType>>>() ;
			if( classMapFieldListCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			classMapFieldListCache.set(new HashMap<String,LinkedList<ClassFieldType>>());
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
		
		if( jsonByteArrayBuilderCache == null ) {
			jsonByteArrayBuilderCache = new ThreadLocal<OkJsonCharArrayBuilder>() ;
			if( jsonByteArrayBuilderCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			jsonCharArrayBuilder = new OkJsonCharArrayBuilder(1024) ;
			if( jsonCharArrayBuilder == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			jsonByteArrayBuilderCache.set(jsonCharArrayBuilder);
		} else {
			jsonCharArrayBuilder = jsonByteArrayBuilderCache.get() ;
		}
		jsonCharArrayBuilder.setLength(0);
		
		if( fieldByteArrayBuilderCache == null ) {
			fieldByteArrayBuilderCache = new ThreadLocal<OkJsonCharArrayBuilder>() ;
			if( fieldByteArrayBuilderCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldCharArrayBuilder = new OkJsonCharArrayBuilder(1024) ;
			if( fieldCharArrayBuilder == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldByteArrayBuilderCache.set(fieldCharArrayBuilder);
		}
		
		if( basicTypeClassMapBooleanCache == null ) {
			basicTypeClassMapBooleanCache = new ThreadLocal<HashMap<Class,Boolean>>() ;
			if( basicTypeClassMapBooleanCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			basicTypeClassMapString = new HashMap<Class,Boolean>() ;
			if( basicTypeClassMapString == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			basicTypeClassMapString.put( String.class, new Boolean(true) );
			basicTypeClassMapString.put( Byte.class, new Boolean(true) );
			basicTypeClassMapString.put( Short.class, new Boolean(true) );
			basicTypeClassMapString.put( Integer.class, new Boolean(true) );
			basicTypeClassMapString.put( Long.class, new Boolean(true) );
			basicTypeClassMapString.put( Float.class, new Boolean(true) );
			basicTypeClassMapString.put( Double.class, new Boolean(true) );
			basicTypeClassMapString.put( Boolean.class, new Boolean(true) );
			basicTypeClassMapBooleanCache.set(basicTypeClassMapString);
		}
		
		if( prettyFormatEnable ) {
			jsonCharArrayBuilder.appendCharArray( "{\n".toCharArray() );
		} else {
			jsonCharArrayBuilder.appendChar( '{' );
		}
		
		errorCode = objectToPropertiesString( object, jsonCharArrayBuilder, 1 );
		if( errorCode != 0 )
			return null;
		
		if( prettyFormatEnable ) {
			jsonCharArrayBuilder.appendCharArray( "}\n".toCharArray() );
		} else {
			jsonCharArrayBuilder.appendChar( '}' );
		}
		
		return jsonCharArrayBuilder.toString();
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

class OkJsonCharArrayBuilder {
	
	private char[]		buf ;
	private int			bufSize ;
	private int			bufLength ;
	
	final private static String	TABS = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" ;
	
	public OkJsonCharArrayBuilder() {
		this( 16 );
	}
	
	public OkJsonCharArrayBuilder( int initBufSize ) {
		this.buf = new char[ initBufSize ] ;
		this.bufSize = initBufSize ;
		this.bufLength = 0 ;
	}
	
	private void resize( int newSize ) {
		char[]		newBuf ;
		int			newBufSize ;

		if( bufSize < 10240240 ) {
			newBufSize = bufSize * 2 ;
		} else {
			newBufSize = bufSize + 10240240 ;
		}
		if( newBufSize < newSize )
			newBufSize = newSize ;
		newBuf = new char[ newBufSize ] ;
		buf = newBuf ;
		bufSize = newBufSize ;
	}
	
	public OkJsonCharArrayBuilder appendChar( char c ) {
		int		newBufLength = bufLength + 1 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = c ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendCharArray( char[] charArray ) {
		int		newBufLength = bufLength + charArray.length ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		System.arraycopy( charArray, 0, buf, bufLength, charArray.length ); bufLength = newBufLength ;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendBytesFromOffsetWithLength( char[] charArray, int offset, int len ) {
		int		newBufLength = bufLength + len ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		System.arraycopy( charArray, offset, buf, bufLength, len ); bufLength = newBufLength ;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendTabs( int tabCount ) {
		int		newBufLength = bufLength + tabCount ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		if( tabCount <= TABS.length() ) {
			System.arraycopy( TABS.toCharArray(), 0, buf, bufLength, tabCount); bufLength+=tabCount;
		} else {
			for( int i = 1 ; i < tabCount ; i++ ) {
				buf[bufLength] = '\t' ; bufLength++;
			}
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndOpenByte( char[] name, char c ) {
		int		newBufLength = bufLength + name.length+4 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = c ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndOpenBytePretty( char[] name, char c ) {
		int		newBufLength = bufLength + name.length+7 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = c ; bufLength++;
		buf[bufLength] = '\n' ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendCloseByte( char c, boolean isNotLastLine ) {
		int		newBufLength = bufLength + 2 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = c ; bufLength++;
		if( isNotLastLine ) {
			buf[bufLength] = ',' ; bufLength++;
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendCloseBytePretty( char c, boolean isNotLastLine ) {
		int		newBufLength = bufLength + 4 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = c ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		if( isNotLastLine ) {
			buf[bufLength] = ',' ; bufLength++;
		}
		buf[bufLength] = '\n' ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndValue( char[] name, char[] value, boolean isNotLastLine ) {
		int		newBufLength = bufLength + name.length+value.length+5 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		System.arraycopy( value, 0, buf, bufLength, value.length ); bufLength+=value.length;
		buf[bufLength] = ' ' ; bufLength++;
		if( isNotLastLine ) {
			buf[bufLength] = ',' ; bufLength++;
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndValuePretty( char[] name, char[] value, boolean isNotLastLine ) {
		int		newBufLength = bufLength + name.length+value.length+6 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length);
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		System.arraycopy( value, 0, buf, bufLength, value.length ); bufLength+=value.length;
		if( isNotLastLine ) {
			buf[bufLength] = ',' ; bufLength++;
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndString( char[] name, char[] str, boolean isNotLastLine ) {
		int		newBufLength = bufLength + name.length+str.length+7 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( str, 0, buf, bufLength, str.length ); bufLength+=str.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		if( isNotLastLine ) {
			buf[bufLength] = ',' ; bufLength++;
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndStringPretty( char[] name, char[] str, boolean isNotLastLine ) {
		int		newBufLength = bufLength + name.length+str.length+8 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length);
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( str, 0, buf, bufLength, str.length ); bufLength+=str.length;
		buf[bufLength] = '"' ; bufLength++;
		if( isNotLastLine ) {
			buf[bufLength] = ',' ; bufLength++;
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonValue( char[] value, boolean isNotLastLine ) {
		int		newBufLength = bufLength + value.length+1 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		System.arraycopy( value, 0, buf, bufLength, value.length ); bufLength+=value.length;
		if( isNotLastLine ) {
			buf[bufLength] = ',' ; bufLength++;
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonValuePretty( char[] value, boolean isNotLastLine ) {
		int		newBufLength = bufLength + value.length+3 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		System.arraycopy( value, 0, buf, bufLength, value.length ); bufLength+=value.length;
		if( isNotLastLine ) {
			buf[bufLength] = ' ' ; bufLength++;
			buf[bufLength] = ',' ; bufLength++;
		}
		buf[bufLength] = ' ' ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonString( char[] str, boolean isNotLastLine ) {
		int		newBufLength = bufLength + str.length+3 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( str, 0, buf, bufLength, str.length ); bufLength+=str.length;
		buf[bufLength] = '"' ; bufLength++;
		if( isNotLastLine ) {
			buf[bufLength] = ',' ; bufLength++;
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonStringPretty( char[] str, boolean isNotLastLine ) {
		int		newBufLength = bufLength + str.length+5 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( str, 0, buf, bufLength, str.length ); bufLength+=str.length;
		buf[bufLength] = '"' ; bufLength++;
		if( isNotLastLine ) {
			buf[bufLength] = ' ' ; bufLength++;
			buf[bufLength] = ',' ; bufLength++;
		}
		buf[bufLength] = ' ' ; bufLength++;
		
		return this;
	}
	
	public int getLength() {
		return bufLength;
	}
	
	public void setLength( int length ) {
		bufLength = length ;
	}
	
	@Override
	public String toString() {
		return new String( buf, 0, bufLength ) ;
	}
}
