package com.ns.design.pattern.abstractfactory;

/**
 * @author ns
 * @date 2021/3/22  19:34
 * @since V1.0
 */
public abstract class AbstractFactory {
	
	public void init() {
		System.out.println("工厂类初始化");
	}
	
	protected abstract IHome buildHome();
	
	protected abstract IYard waiYard();
}
