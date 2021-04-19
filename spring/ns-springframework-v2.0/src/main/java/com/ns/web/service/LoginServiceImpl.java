package com.ns.web.service;

import com.ns.springframework.annotation.NsAutowired;
import com.ns.springframework.annotation.NsService;
import com.ns.web.api.ILoginService;
import com.ns.web.api.ITestService;

/**
 * @author ns
 * @date 2021/4/15  13:41
 */
@NsService
public class LoginServiceImpl implements ILoginService {
	
	@NsAutowired
	private ITestService testService;
	
	@Override
	public void login() {
		testService.test("调用testService接口测试登录");
	}

	@Override
	public String test() {
		return "真正的登录逻辑";
	}
}
