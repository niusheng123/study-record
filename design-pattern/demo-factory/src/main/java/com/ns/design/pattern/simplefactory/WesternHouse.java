package com.ns.design.pattern.simplefactory;

/**
 * @author ns
 * @date 2021/3/22  18:56
 * @since V1.0
 */
public class WesternHouse implements IHome {
	
	@Override
	public String buildHouse() {
		return "盖座小洋楼，美滋滋";
	}
}
