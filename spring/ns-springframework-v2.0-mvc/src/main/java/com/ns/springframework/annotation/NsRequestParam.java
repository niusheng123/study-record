package com.ns.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author ns
 * @date 2021/4/12  19:30
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsRequestParam {
	
	String value() default "";
	
	boolean required() default true;
}
