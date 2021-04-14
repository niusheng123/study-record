package com.ns.design.pattern.adapter;

import com.ns.design.pattern.adapter.classadapter.ThirdLoginAdapter;

/**
 * @author ns
 * @date 2021/3/26  13:40
 */
public class ClassAdapterTest {

	public static void main(String[] args) {
		ThirdLoginAdapter thirdLogin = new ThirdLoginAdapter();
		thirdLogin.login("zhangsan", "111");
	}
}
