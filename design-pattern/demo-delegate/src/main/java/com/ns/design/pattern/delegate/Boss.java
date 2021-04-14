package com.ns.design.pattern.delegate;

/**
 * @author ns
 * @date 2021/4/2  19:21
 */
public class Boss implements IEmployee {
	
	private Manager manager;

	public Boss(Manager manager) {
		this.manager = manager;
	}

	public void work(String task) {
		System.out.println("我是Boss，现在来了个单子，需要"+ task + "开发人员来处理");
		manager.work(task);
	}
}
