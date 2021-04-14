package com.ns.design.pattern.strategy;

/**
 * @author ns
 * @date 2021/4/6  18:30
 */
public class TestStrategy {

	public static void main(String[] args) {
		PayStrategy.getInstance("weChat").pay();
		PayStrategy.getInstance("aliPay").pay();
		PayStrategy.getInstance("unionPay").pay();
	}
}
