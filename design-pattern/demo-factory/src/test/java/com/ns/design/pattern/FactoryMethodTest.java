package com.ns.design.pattern;

import com.ns.design.pattern.factorymethod.BungalowHouseFactory;
import com.ns.design.pattern.factorymethod.TileRoofedHouseFactory;
import com.ns.design.pattern.factorymethod.WesternHouseFactory;

/**
 * @author ns
 * @date 2021/3/22  19:28
 */
public class FactoryMethodTest {

	public static void main(String[] args) {
		BungalowHouseFactory bungalowHouseFactory = new BungalowHouseFactory();
		System.out.println(bungalowHouseFactory.build().buildHouse());
		TileRoofedHouseFactory tileRoofedHouseFactory = new TileRoofedHouseFactory();
		System.out.println(tileRoofedHouseFactory.build().buildHouse());
		WesternHouseFactory westernHouseFactory = new WesternHouseFactory();
		System.out.println(westernHouseFactory.build().buildHouse());
	}
}
