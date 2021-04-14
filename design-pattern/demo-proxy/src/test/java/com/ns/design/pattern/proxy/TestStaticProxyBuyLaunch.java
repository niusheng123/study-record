package com.ns.design.pattern.proxy;

import com.ns.design.pattern.proxy.staticproxy.XiaoWang;
import com.ns.design.pattern.proxy.staticproxy.XiaoZhang;

/**
 * @author ns
 * @date 2021/3/25  17:12
 */
public class TestStaticProxyBuyLaunch {

	public static void main(String[] args) {
		XiaoWang xiaoWang = new XiaoWang(new XiaoZhang());
		xiaoWang.buyLaunch();
	}
}
