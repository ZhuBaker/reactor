package com.qf.reactor.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;


@Target({ TYPE,ANNOTATION_TYPE, METHOD, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Event {

	
	String reactor() default "ringBufferPool" ; //dispatch的名称
}
