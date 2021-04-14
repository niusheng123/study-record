package com.ns.design.pattern.proxy.dynamic.cglibproxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author ns
 * @date 2021/3/25  17:49
 */
public class CglibProxy implements MethodInterceptor {
	
	public Object getInstance(Class<?> clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		return enhancer.create();
	}
	
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		before();
		Object obj = methodProxy.invokeSuper(o, objects);
		after();
		return obj;
	}

	private void before() {
		System.out.println("外卖小哥：等着接单");
	}

	private void after() {
		System.out.println("外卖小哥：已接到订单，准备取货");
	}
}
