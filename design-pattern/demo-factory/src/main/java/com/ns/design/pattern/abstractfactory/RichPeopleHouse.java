package com.ns.design.pattern.abstractfactory;

/**
 * @author ns
 * @date 2021/3/22  19:52
 * @since V1.0
 */
public class RichPeopleHouse implements IHome {
	
	@Override
	public void buildHouse() {
		System.out.println("有点小钱，盖个大房子~~");
	}
}
