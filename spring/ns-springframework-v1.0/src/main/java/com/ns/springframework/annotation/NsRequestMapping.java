package com.ns.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author ns
 * @date 2021/4/12  19:25
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsRequestMapping {
	
	String value() default "";
}
