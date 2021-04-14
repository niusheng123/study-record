package com.ns.design.pattern.singleton.lazy;

/**
 * @author ns
 * @date 2021/3/24  19:14
 * @since V1.0
 */
public class LazySyncSingleton {
	
	private static LazySyncSingleton lazySyncSingleton;
	
	private LazySyncSingleton() {}
	
	public synchronized static LazySyncSingleton getInstance() {
		if (lazySyncSingleton == null) {
			lazySyncSingleton = new LazySyncSingleton();
		}
		return lazySyncSingleton;
	}
}
