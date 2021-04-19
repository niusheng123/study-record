package com.ns.springframework.webmvc;

import java.util.Map;

/**
 * 视图对象
 * @author ns
 * @date 2021/4/19  18:22
 */
public class NsModelAndView {
	
	private String viewName;
	
	private Map<String, ?> model;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Map<String, ?> getModel() {
		return model;
	}

	public void setModel(Map<String, ?> model) {
		this.model = model;
	}
}
