package com.ns.design.pattern.factorymethod;

/**
 * @author ns
 * @date 2021/3/22  19:26
 * @since V1.0
 */
public class WesternHouseFactory implements IHomeFactory {
	
	@Override
	public IHome build() {
		return new WesternHouse();
	}
}
