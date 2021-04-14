package com.ns.design.pattern.responsibility;

/**
 * @author ns
 * @date 2021/4/7  9:45
 */
public class ValidateBlackUser extends AbstractHandler {
	
	private static final String BLACK_USER = "xiaowang";
	
	@Override
	public void doHandler(User user) {
		if (BLACK_USER.equals(user.getUsername())) {
			System.out.println("黑名单校验未通过");
			return;
		}
		System.out.println("黑名单验证通过");
		chain.doHandler(user);
	}
}
