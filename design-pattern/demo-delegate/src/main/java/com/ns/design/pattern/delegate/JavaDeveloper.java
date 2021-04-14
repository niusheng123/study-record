package com.ns.design.pattern.delegate;

/**
 * @author ns
 * @date 2021/4/2  19:26
 */
public class JavaDeveloper implements IEmployee {
	
	public void work(String task) {
		System.out.println("我是Java开发牛大大，外包干活了");	
	}
}
