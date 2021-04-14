package com.ns.design.pattern.singleton.lazy;

/**
 * @author ns
 * @date 2021/3/24  19:32
 * @since V1.0
 */
public class InnerClassSingleton {
	
	private InnerClassSingleton(){
		if (InnerClass.INNER_CLASS_SINGLETON != null) {
			throw new RuntimeException("不允许创建多个单例对象");
		}
	}
	
	public static InnerClassSingleton getInstance() {
		return InnerClass.INNER_CLASS_SINGLETON;
	}
	
	private static class InnerClass {
		private static final InnerClassSingleton INNER_CLASS_SINGLETON = new InnerClassSingleton();
	}
}
