package com.ns.design.pattern.proxy.staticproxy;

/**
 * 代理对象小王
 * @author ns
 * @date 2021/3/25  17:09
 * @since V1.0
 */
public class XiaoWang implements IBuyLaunch{
	
	private XiaoZhang xiaoZhang;

	public XiaoWang(XiaoZhang xiaoZhang) {
		this.xiaoZhang = xiaoZhang;
	}

	@Override
	public void buyLaunch() {
		System.out.println("小王：我去买饭");
		// 帮小张去买饭
		xiaoZhang.buyLaunch();
		System.out.println("小王：好，我去帮你买");
	}
}
