package com.ns.design.pattern.abstractfactory;

/**
 * @author ns
 * @date 2021/3/22  19:58
 * @since V1.0
 */
public class RichPeopleFactory extends AbstractFactory {
	@Override
	public IHome buildHome() {
		return new RichPeopleHouse();
	}

	@Override
	public IYard waiYard() {
		return new RichPeopleYard();
	}
}
