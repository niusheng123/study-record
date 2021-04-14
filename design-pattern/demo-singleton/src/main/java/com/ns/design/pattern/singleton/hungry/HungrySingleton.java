package com.ns.design.pattern.singleton.hungry;

/**
 * @author ns
 * @date 2021/3/24  19:00
 * @since V1.0
 */
public class HungrySingleton {
	
	private static final HungrySingleton hungrySingleton = new HungrySingleton();
	
	private HungrySingleton() {}
	
	public static HungrySingleton getInstance() {
		return hungrySingleton;
	}
}
