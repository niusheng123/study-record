package com.ns.design.pattern.factorymethod;

/**
 * @author ns
 * @date 2021/3/22  19:24
 * @since V1.0
 */
public class BungalowHouseFactory implements IHomeFactory {
	
	@Override
	public IHome build() {
		return new BungalowHouse();
	}
}
