package com.ns.design.pattern.responsibility;

/**
 * @author ns
 * @date 2021/4/7  9:51
 */
public class RequestHandler {
	
	private static AbstractHandler abstractHandler;
	
	static {
		AbstractHandler.Builder builder = new AbstractHandler.Builder();
		builder.addHandler(new ValidatePassword())
				.addHandler(new ValidateCaptcha())
				.addHandler(new ValidateBlackUser())
				.addHandler(new MenuService());
		abstractHandler = builder.builder();
	}
	
	public static void doRequest(User user) {
		abstractHandler.doHandler(user);
	}
}
