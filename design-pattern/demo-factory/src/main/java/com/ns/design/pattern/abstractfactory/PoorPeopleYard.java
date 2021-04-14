package com.ns.design.pattern.abstractfactory;

/**
 * @author ns
 * @date 2021/3/22  19:51
 * @since V1.0
 */
public class PoorPeopleYard implements IYard {
	@Override
	public void waiYard() {
		System.out.println("穷人，没钱盖房子，就只剩下大院子了！！！");
	}
}
