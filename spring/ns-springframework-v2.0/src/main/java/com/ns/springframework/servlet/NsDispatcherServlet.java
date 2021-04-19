package com.ns.springframework.servlet;

import com.ns.springframework.annotation.NsController;
import com.ns.springframework.annotation.NsRequestMapping;
import com.ns.springframework.beans.NsBeanWrapper;
import com.ns.springframework.context.NsApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author ns
 * @date 2021/4/12  19:05
 */
public class NsDispatcherServlet extends HttpServlet {

	/**
	 * 应用上下文，提供getBean()方法
	 */
	private NsApplicationContext applicationContext;

	/**
	 * handlerMapping，包含类实例，方法路径，参数索引
	 */
	private List<Handler> handlerList = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			doDispatcher(req, resp);
		} catch (Exception e) {
			resp.getWriter().write("500 Error");
		}
	}

	/**
	 * 请求调用
	 * @param req HttpServletRequest 
	 * @param resp HttpServletResponse
	 */
	private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json;charset=UTF-8");
		
		Handler handler = this.getHandler(req);
		
		// 如果没有handler，直接返回404
		if (handler == null) {
			resp.getWriter().write("404 Not Found");
			return;
		}
		// 处理请求参数
		Object[] params = this.getRequestParams(req, handler);
		Object result = handler.method.invoke(handler.controller, params);
		resp.getWriter().write(result.toString());
	}

	/**
	 * 根据请求的url获取对应的实例和参数
	 * @param req 请求头
	 * @return Handler
	 */
	private Handler getHandler(HttpServletRequest req) {
		if (handlerList.isEmpty()) {
			return null;
		}
		// 拿到url并去除url的项目路径得到真正的方法请求路径
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		uri = uri.replaceAll(contextPath, "").replaceAll("/+", "/");
		// 遍历所有的url，拿到匹配的
		for (Handler handler : handlerList) {
			if (handler.urlPatter.matcher(uri).matches()) {
				return handler;
			}
		}
		return null;
	}

	/**
	 * 根据形参列表和请求头返回带有顺序的实参列表
	 * @param req 请求头
	 * @param handler Handler对象
	 * @return 实参列表
	 */
	@SuppressWarnings("unchecked")
	private Object[] getRequestParams(HttpServletRequest req, Handler handler) {
		// 所有的形参类型
		Class<?>[] parameterTypes = handler.method.getParameterTypes();
		// 返回值
		Object[] paramValues = new Object[parameterTypes.length];
		// 拿到前台请求参数
		Map<String,String[]> reqParameterMap = req.getParameterMap();
		for (Map.Entry<String, String[]> entry : reqParameterMap.entrySet()) {
			if (!handler.paramIndexMapping.containsKey(entry.getKey())) {
				continue;
			}
			int index = handler.paramIndexMapping.get(entry.getKey());
			// 转换参数值为指定类型
			paramValues[index] = convertParamValue(parameterTypes[index], entry.getValue());
		}
		return paramValues;
	}

	/**
	 * 转换参数值为指定类型
	 * @param parameterType 对应的类型
	 * @param value 值
	 * @return Object
	 */
	private Object convertParamValue(Class<?> parameterType, String[] value) {
		if (parameterType == Integer.class) {
			return Integer.valueOf(value[0]);
		} else if (parameterType == Boolean.class) {
			return Boolean.valueOf(value[0]);
		}
		return value[0];
	}

	@Override
	public void init(ServletConfig config) {
		String applicationName = config.getInitParameter("configLocation").replace("classpath:", "");
		applicationContext = new NsApplicationContext(applicationName);
		// 5.初始化HandlerMapping
		doInitHandlerMapping();
	}

	/**
	 * 初始化handlerMapping，生成requestMapping和method的映射关系
	 */
	private void doInitHandlerMapping() {
		Map<String, NsBeanWrapper> ioc = applicationContext.getIoc();
		if (ioc.isEmpty()) {
			return;
		}
		for (Map.Entry<String, NsBeanWrapper> entry : ioc.entrySet()) {
			Class<?> clazz = entry.getValue().getWrapperClass();
			// 判断此容器是否为控制层
			if (!clazz.isAnnotationPresent(NsController.class)) {
				continue;
			}
			String baseUrl = "";
			// 获取类对应的RequestMapping
			if (clazz.isAnnotationPresent(NsRequestMapping.class)) {
				baseUrl = clazz.getAnnotation(NsRequestMapping.class).value();
			}
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				if (!method.isAnnotationPresent(NsRequestMapping.class)) {
					continue;
				}
				// 拼接多个/，防止有人忘了写
				String methodUrl = "/" + baseUrl + "/" + method.getAnnotation(NsRequestMapping.class).value();
				// 重复的/ 替换成单个
				Pattern pattern = 	Pattern.compile(methodUrl.replaceAll("/+", "/"));
				handlerList.add(new Handler(entry.getValue().getWrapperInstance(), pattern, method));
			}
		}
	}

}
