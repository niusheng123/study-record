package com.ns.design.pattern.singleton.lazy;

/**
 * @author ns
 * @date 2021/3/24  19:18
 * @since V1.0
 */
public class DoubleCheckLazySingleton {
	
	private static DoubleCheckLazySingleton doubleCheckLazySingleton;
	
	private DoubleCheckLazySingleton() {}
	
	public static DoubleCheckLazySingleton getInstance() {
		if (doubleCheckLazySingleton == null) {
			synchronized (DoubleCheckLazySingleton.class) {
				if (doubleCheckLazySingleton == null) {
					doubleCheckLazySingleton = new DoubleCheckLazySingleton();
				}
			}
		}
		return doubleCheckLazySingleton;
	}
}
