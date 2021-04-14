package com.ns.design.pattern.templatemethod;

/**
 * @author ns
 * @date 2021/4/3  15:25
 */
public class XiaoMingFindWork extends AbstractFindWord {

	public XiaoMingFindWork() {
		System.out.println("我是小明，开始找工作");
	}

	@Override
	protected boolean isInterviewSuccess() {
		return false;
	}
}
