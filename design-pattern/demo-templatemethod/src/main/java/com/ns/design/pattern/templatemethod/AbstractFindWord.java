package com.ns.design.pattern.templatemethod;

/**
 * @author ns
 * @date 2021/4/3  15:19
 */
public abstract class AbstractFindWord {
	
	protected void resume() {
		System.out.println("投简历");
	}
	
	protected void communicateWithBoss() {
		System.out.println("Boss看到简历，跟Boss谈");
	}
	
	protected void appointmentInterview() {
		System.out.println("聊得还可以，约面试");
	}
	
	protected void afterInterview() {
		System.out.println("面试结束");
	}
	
	protected boolean isInterviewSuccess() {
		return true;
	}
	
	protected void entry() {
		System.out.println("办理入职");
	}
	
	protected void startWork() {
		System.out.println("开始上班干活");
	}
	
	protected void findWork() {
		resume();
		communicateWithBoss();
		appointmentInterview();
		afterInterview();
		if (isInterviewSuccess()) {
			System.out.println("面试成功，准备办理入职");
		} else {
			System.out.println("面试失败，继续找工作吧");
			return;
		}
		entry();
		startWork();
	}
	
	
}
