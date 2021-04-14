package com.ns.design.pattern.delegate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ns
 * @date 2021/4/2  19:22
 */
public class Manager implements IEmployee {
	
	private static Map<String, IEmployee> employeeMap = new HashMap<String, IEmployee>();
	
	static {
		employeeMap.put("vue", new VueDeveloper());
		employeeMap.put("java", new JavaDeveloper());
	}
	
	public void work(String task) {
		if (!employeeMap.containsKey(task)) {
			System.out.println("我是人力总监，没有能做这个任务的人！！！！");
			return;
		}
		System.out.println("我是人力总监，找到可以做这件事的人");
		employeeMap.get(task).work(task);
	}
}
