package com.ns.design.pattern.flyweight;

/**
 * @author ns
 * @date 2021/3/30  18:45
 */
public class TestInteger {

	public static void main(String[] args) {
		Integer a = Integer.valueOf(100);
		Integer b = 100;
		Integer c = Integer.valueOf(1000);
		Integer d = 1000;
		System.out.println(a == b);		// true
		System.out.println(c == d);		// false
	}
}
