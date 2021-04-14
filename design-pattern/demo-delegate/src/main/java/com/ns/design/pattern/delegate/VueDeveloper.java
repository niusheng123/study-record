package com.ns.design.pattern.delegate;

/**
 * 前端开发
 * @author ns
 * @date 2021/4/2  19:25
 */
public class VueDeveloper implements IEmployee {
	
	public void work(String task) {
		System.out.println("我是Vue开发张大大，封闭开发去咯");
	}
}
