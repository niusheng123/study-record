package com.ns.springframework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 缓存控制层类实例、方法以及请求url
 * @author ns
 * @date 2021/4/19  18:17
 */
public class NsHandlerMapping {

	/**
	 * controller实例
	 */
	private Object controller;

	/**
	 * 调用方法
	 */
	private Method method;

	/**
	 * 请求url正则
	 */
	private Pattern pattern;


	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
}
