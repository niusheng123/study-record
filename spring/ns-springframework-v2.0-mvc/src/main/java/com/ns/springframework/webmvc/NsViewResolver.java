package com.ns.springframework.webmvc;

import java.io.File;
import java.util.Locale;

/**
 * 视图解析器
 * @author ns
 * @date 2021/4/19  18:26
 */
public class NsViewResolver {
	
	private static final String DEFAULT_TEMPLATE_SUFFIX = ".html";
	
	private File templateRootFile;

	public NsViewResolver(String templateRoot) {
		String templateFilePath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
		this.templateRootFile = new File(templateFilePath);
	}
	
	public NsView resolveViewName(String viewName, Locale locale) {
		if (viewName == null || "".equals(viewName.trim())) {
			return null;
		}
		viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
		File templateFile = new File((templateRootFile.getPath() + "/" + viewName).replaceAll("/+", "/"));
		return new NsView(templateFile);
	}
}
