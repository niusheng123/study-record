package com.ns.design.pattern.proxy.dynamic.jdkproxy;

/**
 * @author ns
 * @date 2021/3/25  17:24
 */
public class XiaoZhang implements IBuyLaunch {
	
	@Override
	public void buyLaunch() {
		System.out.println("小张：我在大排档点了一份米饭");	
	}
}
