package com.ns.design.pattern.simplefactory;

/**
 * 简单工厂类
 * - 由工厂类来创建具体的实例对象
 * - 适用于工厂类负责创建的对象较少的场景，客户端只需要传入对应的参数，不关系具体的创建过程
 * @author ns
 * @date 2021/3/22  18:52
 * @since V1.0
 */
public class SimpleFactory {
	
	public static IHome getInstance(Class<? extends IHome> clazz) {
		if (clazz != null) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("这是啥品种房？");
	}
}
