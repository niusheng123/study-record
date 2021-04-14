package com.ns.design.pattern.responsibility;

import java.util.Arrays;
import java.util.List;

/**
 * @author ns
 * @date 2021/4/7  9:48
 */
public class MenuService extends AbstractHandler {
	
	private static final List<String> MENU_LIST = Arrays.asList("用户菜单","角色菜单","部门菜单");
	
	@Override
	public void doHandler(User user) {
		System.out.println("根据用户" +user.getUsername()+ "获取菜单->" + MENU_LIST);	
	}
}
