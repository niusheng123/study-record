package com.ns.design.pattern.delegate;

/**
 * 所有人都是员工，都要干活
 * @author ns
 * @date 2021/4/2  19:21
 */
public interface IEmployee {

	/**
	 * 工作
	 */
	void work(String task);
}
