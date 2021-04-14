package com.ns.design.pattern.factorymethod;

/**
 * 平房
 * @author ns
 * @date 2021/3/22  18:54
 * @since V1.0
 */
public class BungalowHouse  implements IHome{
	
	@Override
	public String buildHouse() {
		return "盖个平房";
	}
}
