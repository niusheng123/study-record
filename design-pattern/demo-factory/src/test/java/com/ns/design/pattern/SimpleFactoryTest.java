package com.ns.design.pattern;

import com.ns.design.pattern.simplefactory.IHome;
import com.ns.design.pattern.simplefactory.SimpleFactory;
import com.ns.design.pattern.simplefactory.WesternHouse;

class SimpleFactoryTest {

	public static void main(String[] args) {
		IHome home = SimpleFactory.getInstance(WesternHouse.class);
		System.out.println(home.buildHouse());;
	}

}
