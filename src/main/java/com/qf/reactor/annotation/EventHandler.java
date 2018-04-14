package com.qf.reactor.annotation;

import com.qf.reactor.model.Scope;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;




@Target({ TYPE,ANNOTATION_TYPE, METHOD, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EventHandler {
	
	Reactor reactor() default @Reactor ;//
	
	
	Scope scope() default Scope.SINGLETON;
	 
	
	Class<?> key() default Object.class; //当前
	

}


