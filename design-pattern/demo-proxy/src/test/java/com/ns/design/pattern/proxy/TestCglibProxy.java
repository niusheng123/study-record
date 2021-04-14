package com.ns.design.pattern.proxy;

import com.ns.design.pattern.proxy.dynamic.cglibproxy.CglibProxy;
import com.ns.design.pattern.proxy.dynamic.cglibproxy.XiaoWang;

/**
 * @author ns
 * @date 2021/3/25  17:55
 */
public class TestCglibProxy {

	public static void main(String[] args) {
		CglibProxy proxy = new CglibProxy();
		XiaoWang xiaoWang = (XiaoWang) proxy.getInstance(XiaoWang.class);
		xiaoWang.buyLaunch();
	}
}
