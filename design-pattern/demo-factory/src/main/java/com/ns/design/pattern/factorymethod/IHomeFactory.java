package com.ns.design.pattern.factorymethod;

/**
 * 工厂方法模式
 * - 定义创建对象的接口，让实现这个接口的类决定创建哪个对象
 * 优点：符合开闭原则
 * 缺点：类越来越多
 * @author ns
 * @date 2021/3/22  19:23
 * @since V1.0
 */
public interface IHomeFactory {
	
	IHome build();
}
