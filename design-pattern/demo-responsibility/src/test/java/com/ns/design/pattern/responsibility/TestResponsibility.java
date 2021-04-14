package com.ns.design.pattern.responsibility;

/**
 * @author ns
 * @date 2021/4/7  9:54
 */
public class TestResponsibility {

	public static void main(String[] args) {
		User user = new User("lisi", "123456", "qwer");
		RequestHandler.doRequest(user);
	}
}
