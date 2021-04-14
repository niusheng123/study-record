package com.ns.design.pattern.adapter.classadapter;

/**
 * @author ns
 * @date 2021/3/26  13:36
 */
public class ThirdLoginAdapter extends NormalLogin implements ILogin {

	@Override
	public void login(String userName, String password) {
		System.out.println("第三方登录，此时用户名和密码是Aes对称加密");
		System.out.println("加密第三方用户名和密码，重新进行md5加密，调用原登录逻辑");
		super.login(userName, password);
	}
}
