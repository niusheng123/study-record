package com.ns.design.pattern;

import com.ns.design.pattern.abstractfactory.PoorPeopleFactory;
import com.ns.design.pattern.abstractfactory.RichPeopleFactory;

/**
 * @author ns
 * @date 2021/3/22  19:59
 * @since V1.0
 */
public class AbstractFactoryTest {

	public static void main(String[] args) {
		RichPeopleFactory richPeopleFactory = new RichPeopleFactory();
		richPeopleFactory.buildHome().buildHouse();
		richPeopleFactory.waiYard().waiYard();

		PoorPeopleFactory poorPeopleFactory = new PoorPeopleFactory();
		poorPeopleFactory.buildHome().buildHouse();
		poorPeopleFactory.waiYard().waiYard();
	}
}
