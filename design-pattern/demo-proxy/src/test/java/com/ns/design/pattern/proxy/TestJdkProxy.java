package com.ns.design.pattern.proxy;

import com.ns.design.pattern.proxy.dynamic.jdkproxy.IBuyLaunch;
import com.ns.design.pattern.proxy.dynamic.jdkproxy.TakeAwayBoy;
import com.ns.design.pattern.proxy.dynamic.jdkproxy.XiaoWang;

/**
 * @author ns
 * @date 2021/3/25  17:30
 */
public class TestJdkProxy {

	public static void main(String[] args) {
		TakeAwayBoy takeAwayBoy = new TakeAwayBoy();
		IBuyLaunch buyLaunch = takeAwayBoy.getInstance(new XiaoWang());
		buyLaunch.buyLaunch();
	}
}
