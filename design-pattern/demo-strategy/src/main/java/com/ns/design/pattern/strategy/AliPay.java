package com.ns.design.pattern.strategy;

/**
 * @author ns
 * @date 2021/4/6  18:26
 */
public class AliPay implements IPay{
	
	@Override
	public void pay() {
		System.out.println("支付宝支付");	
	}
}
