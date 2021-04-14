package com.ns.design.pattern.strategy;

/**
 * @author ns
 * @date 2021/4/6  18:26
 */
public class UnionPay implements IPay {
	
	@Override
	public void pay() {
		System.out.println("银联支付");	
	}
}
