package com.ns.design.pattern.adapter.classadapter;

/**
 * @author ns
 * @date 2021/3/26  13:33
 */
public interface ILogin {

	/**
	 * 登录
	 * @param userName md5用户名
	 * @param password md5密码
	 */
	void login(String userName, String password);
}
