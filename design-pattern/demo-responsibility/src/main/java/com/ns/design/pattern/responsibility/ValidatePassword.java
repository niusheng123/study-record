package com.ns.design.pattern.responsibility;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ns
 * @date 2021/4/7  9:36
 */
public class ValidatePassword extends AbstractHandler {

	/**
	 * 密码，就当是从数据库查询出来的
	 */
	private static final String PASSWORD = "123456";
	
	
	@Override
	public void doHandler(User user) {
		if (user == null || StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			System.out.println("用户名和密码不能为空");
			return;
		}
		if (!PASSWORD.equals(user.getPassword())) {
			System.out.println("密码错误");
			return;
		}
		System.out.println("用户名密码验证通过");
		chain.doHandler(user);
	}
}
