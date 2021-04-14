package com.ns.web.service;

import com.ns.springframework.annotation.NsService;
import com.ns.web.api.ITestService;

/**
 * @author ns
 * @date 2021/4/12  19:33
 */
@NsService
public class TestServiceImpl implements ITestService {

	@Override
	public String test(String type) {
		if ("ceshi".equals(type)) {
			return "测试成功";
		}
		return "滴滴，执行成功";
	}
}
