package com.ns.design.pattern.proxy.staticproxy;

/**
 * 目标对象 小张
 * @author ns
 * @date 2021/3/25  17:09
 * @since V1.0
 */
public class XiaoZhang implements IBuyLaunch{
	
	@Override
	public void buyLaunch() {
		System.out.println("小张：我还没起床，但是想吃饭了");	
	}
}
