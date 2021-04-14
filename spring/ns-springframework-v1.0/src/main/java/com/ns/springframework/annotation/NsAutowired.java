package com.ns.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author ns
 * @date 2021/4/12  19:25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsAutowired {
	
	String value() default "";
}
