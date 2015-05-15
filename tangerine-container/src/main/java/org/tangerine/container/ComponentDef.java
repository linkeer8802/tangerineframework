package org.tangerine.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.TYPE})
public @interface ComponentDef {

	String value();
	
	Class<? extends Component>[] depends() default {};
}
