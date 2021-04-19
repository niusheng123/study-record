package com.ns.springframework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ns
 * @date 2021/4/19  18:20
 */
public class NsHandlerAdapter {

	/**
	 * 判断handler是否是NsHandlerMapping类型
	 * @param handler 参数
	 * @return true/false
	 */
	public boolean support(Object handler) {
		return handler instanceof NsHandlerMapping;
	}
	
	NsModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		NsHandlerMapping handlerMapping = (NsHandlerMapping) handler;
		// 参数与索引的映射关系
		Map<String, Integer> paramIndexMapping = new HashMap<>();
		
	}
}
