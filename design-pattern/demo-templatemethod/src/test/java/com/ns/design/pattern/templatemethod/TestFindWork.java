package com.ns.design.pattern.templatemethod;

/**
 * @author ns
 * @date 2021/4/3  15:26
 */
public class TestFindWork {

	public static void main(String[] args) {
		XiaoMingFindWork xiaoMing = new XiaoMingFindWork();
		xiaoMing.findWork();
		XiaoWangFindWork xiaoWang = new XiaoWangFindWork();
		xiaoWang.findWork();
	}
}
