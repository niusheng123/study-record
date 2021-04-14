package com.ns.design.pattern.abstractfactory;

/**
 * @author ns
 * @date 2021/3/22  19:55
 * @since V1.0
 */
public class PoorPeopleFactory extends AbstractFactory {
	@Override
	public IHome buildHome() {
		return new PoorPeopleHouse();
	}

	@Override
	public IYard waiYard() {
		return new PoorPeopleYard();
	}
}
