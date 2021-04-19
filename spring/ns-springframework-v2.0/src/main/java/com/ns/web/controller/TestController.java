package com.ns.web.controller;

import com.ns.springframework.annotation.NsAutowired;
import com.ns.springframework.annotation.NsController;
import com.ns.springframework.annotation.NsRequestMapping;
import com.ns.web.api.ITestService;

import java.util.Arrays;
import java.util.List;

/**
 * @author ns
 * @date 2021/4/12  19:33
 */
@NsController
@NsRequestMapping("/test")
public class TestController {
	
	@NsAutowired
	private ITestService testService;
	
	@NsRequestMapping("/add")
	public String add(String type) {
		System.out.println("新增" + type);
		return testService.test(type);
	}
	
	@NsRequestMapping("/delete")
	public void delete(String id) {
		System.out.println("删除成功：" + id);
	}
	
	@NsRequestMapping("/select")
	public List<String> select(String id) {
		System.out.println("查询by" + id );
		return Arrays.asList("aaa","bbb","ccc");
	}
	
	@NsRequestMapping("/login")
	public String login() {
		return testService.login();
	}
}
