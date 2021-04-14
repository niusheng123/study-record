package com.ns.design.pattern.singleton.hungry;

/**
 * @author ns
 * @date 2021/3/24  19:04
 * @since V1.0
 */
public class HungryStaticSingleton {
	
	private static HungryStaticSingleton HUNGRY_SINGLETON;
	
	static {
		HUNGRY_SINGLETON = new HungryStaticSingleton();
	}
	
	private HungryStaticSingleton() {}
	
	public static HungryStaticSingleton getInstance() {
		return HUNGRY_SINGLETON;
	}
}
