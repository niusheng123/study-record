package com.ns.springframework.annotation;

import java.lang.annotation.*;

/**
 * @author ns
 * @date 2021/4/12  19:24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsController {
	
}
