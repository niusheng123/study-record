package com.ns.design.pattern.factorymethod;

/**
 * 瓦房
 * @author ns
 * @date 2021/3/22  18:53
 * @since V1.0
 */
public class TileRoofedHouse implements IHome{
	
	@Override
	public String buildHouse() {
		return "盖个小瓦房";
	}
}
