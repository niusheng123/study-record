package com.ns.design.pattern.delegate;

/**
 * @author ns
 * @date 2021/4/2  19:32
 */
public class TestDelegate {

	public static void main(String[] args) {
		Boss boss = new Boss(new Manager());
		boss.work("java");
	}
}
