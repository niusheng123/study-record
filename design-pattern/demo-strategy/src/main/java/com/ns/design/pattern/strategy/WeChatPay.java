package com.ns.design.pattern.strategy;

/**
 * @author ns
 * @date 2021/4/6  18:25
 */
public class WeChatPay implements IPay {

	@Override
	public void pay() {
		System.out.println("微信支付");
	}
}
