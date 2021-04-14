package com.ns.design.pattern.proxy.dynamic.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理类 外卖小哥~
 * @author ns
 * @date 2021/3/25  17:23
 */
public class TakeAwayBoy implements InvocationHandler {
	
	private IBuyLaunch object;
	
	public IBuyLaunch getInstance(IBuyLaunch object) {
		this.object = object;
		Class<?> clazz = object.getClass();
		// 创建代理类 -> 参数1：类加载器 参数2 类实现的接口  参数3：实现了InvocationHandler的类
		return (IBuyLaunch) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		before();
		Object obj = method.invoke(object, args);
		after();
		return obj;
	}

	private void before() {
		System.out.println("外卖小哥：在App上等着接单");
	}

	private void after() {
		System.out.println("外卖小哥：已经接到订单，准备取货");
	}
}
