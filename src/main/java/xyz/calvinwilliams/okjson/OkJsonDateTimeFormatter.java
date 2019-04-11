package xyz.calvinwilliams.okjson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)
public @interface OkJsonDateTimeFormatter {
	public String format();
}
