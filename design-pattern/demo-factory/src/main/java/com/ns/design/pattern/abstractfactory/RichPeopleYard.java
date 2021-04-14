package com.ns.design.pattern.abstractfactory;

/**
 * @author ns
 * @date 2021/3/22  19:54
 * @since V1.0
 */
public class RichPeopleYard implements IYard {
	@Override
	public void waiYard() {
		System.out.println("有点小钱全盖房子了，院子都给占没了，哎·");
	}
}
