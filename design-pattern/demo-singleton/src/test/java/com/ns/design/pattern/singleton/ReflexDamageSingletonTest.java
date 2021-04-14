package com.ns.design.pattern.singleton;

import com.ns.design.pattern.singleton.lazy.DoubleCheckLazySingleton;
import com.ns.design.pattern.singleton.lazy.InnerClassSingleton;

import java.lang.reflect.Constructor;

/**
 * @author ns
 * @date 2021/3/24  19:26
 * @since V1.0
 */
public class ReflexDamageSingletonTest {

	public static void main(String[] args) throws Exception {
		Class<?> clazz = InnerClassSingleton.class;
		Constructor constructor = clazz.getDeclaredConstructor();
		constructor.setAccessible(true);
		Object instance1 = constructor.newInstance();
		Object instance2 = constructor.newInstance();
		System.out.println(instance1 == instance2);
	}
}
