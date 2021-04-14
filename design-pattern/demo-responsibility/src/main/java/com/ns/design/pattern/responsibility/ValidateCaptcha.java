package com.ns.design.pattern.responsibility;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ns
 * @date 2021/4/7  9:43
 */
public class ValidateCaptcha extends AbstractHandler {
	
	private static final String CAPTCHA = "qwer";

	@Override
	public void doHandler(User user) {
		if (StringUtils.isBlank(user.getCaptcha()) || !CAPTCHA.equals(user.getCaptcha())) {
			System.out.println("验证码错误");
			return;
		}
		System.out.println("验证码验证通过");
		chain.doHandler(user);
	}
}
