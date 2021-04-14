package com.ns.design.pattern.singleton.lazy;

/**
 * @author ns
 * @date 2021/3/24  19:07
 * @since V1.0
 */
public class LazySingleton {
	
	private static LazySingleton lazySingleton;
	
	private LazySingleton() {}
	
	public static LazySingleton getInstance() {
		if (lazySingleton == null) {
			lazySingleton = new LazySingleton();
		}
		return lazySingleton;
	}
}
