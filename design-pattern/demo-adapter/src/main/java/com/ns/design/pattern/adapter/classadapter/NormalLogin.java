package com.ns.design.pattern.adapter.classadapter;

/**
 * @author ns
 * @date 2021/3/26  13:34
 */
public class NormalLogin implements ILogin {
	
	@Override
	public void login(String userName, String password) {
		System.out.println("正常登录，userName和password是md5加密");	
	}
}
