package org.tangerine.handle.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.tangerine.protocol.Message;

@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RuoteMapping {

	public String value();
	public byte type() default Message.Type.MSG_REQUEST;
}
