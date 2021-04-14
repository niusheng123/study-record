package com.ns.design.pattern.abstractfactory;

/**
 * @author ns
 * @date 2021/3/22  19:37
 * @since V1.0
 */
public class PoorPeopleHouse implements IHome {
	
	@Override
	public void buildHouse() {
		System.out.println("穷人，没啥钱，就盖个小平房吧~~~");
	}
}
