package com.ns.springframework.servlet;

import com.ns.springframework.annotation.NsRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author ns
 * @date 2021/4/14  9:12
 */
public class Handler {

	/**
	 * 方法所在的controller的实例
	 */
	protected Object controller;

	/**
	 * 方法对象
	 */
	protected Method method;

	/**
	 * url路径
	 */
	protected Pattern urlPatter;

	/**
	 * 参数顺序
	 */
	protected Map<String,Integer> paramIndexMapping;

	public Handler(Object controller, Pattern urlPatter, Method method) {
		this.controller = controller;
		this.urlPatter = urlPatter;
		this.method = method;
		paramIndexMapping = new HashMap<>(8);
		setParamIndexMapping(method);
	}

	private void setParamIndexMapping(Method method) {

		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
				paramIndexMapping.put(parameterType.getName(), i);
			} else {
				// 参数注解
				Annotation[][] annotations = method.getParameterAnnotations();
				String paramName = (method.getParameters())[i].getName();
				for (Annotation[] value : annotations) {
					for (Annotation annotation : value) {
						if (annotation instanceof NsRequestParam) {
							// 拿到参数别名
							paramName = ((NsRequestParam) annotation).value();
						}
					}
				}
				paramIndexMapping.put(paramName, i);
			}
		}
	}
}
