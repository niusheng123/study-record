package com.ns.design.pattern.adapter.objectadapter;

/**
 * @author ns
 * @date 2021/3/26  14:03
 */
public class NormalLoan implements ILoan {
	
	@Override
	public int loan() {
		System.out.println("正常借钱，借多少给多少，可借额度：" + 10000);
		return 10000;
	}
}
