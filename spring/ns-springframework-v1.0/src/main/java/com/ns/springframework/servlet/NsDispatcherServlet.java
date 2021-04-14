package com.ns.springframework.servlet;

import com.ns.springframework.annotation.NsAutowired;
import com.ns.springframework.annotation.NsController;
import com.ns.springframework.annotation.NsRequestMapping;
import com.ns.springframework.annotation.NsService;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author ns
 * @date 2021/4/12  19:05
 */
public class NsDispatcherServlet extends HttpServlet {

	/**
	 * 缓存配置文件信息
	 */
	private Properties properties = new Properties();

	/**
	 * 缓存扫描的所有类
	 */
	private List<String> classNames = new ArrayList<>();

	/**
	 * ioc容器，key为首字母小写的类名，value为类对应的实例
	 */
	private Map<String,Object> ioc = new HashMap<>();

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
		
		// 1. 加载配置文件
		doLoadConfig(config);
		// 2. 扫描相关的类
		doScanner(properties.getProperty("componentScan"));
		// 3. 初始化扫描到的类，并放入IOC容器中
		doInstance();
		// 4.完成依赖注入
		doAutowired();
		// 5.初始化HandlerMapping
		doInitHandlerMapping();
	}

	/**
	 * 加载配置文件
	 * @param config 配置信息
	 */
	private void doLoadConfig(ServletConfig config) {
		String applicationName = config.getInitParameter("configLocation").replace("classpath:", "");
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(applicationName);
		try {
			this.properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 扫描相关的类
	 * 将以.class结尾的类添加到缓存中
	 */
	private void doScanner(String packageName) {
		// 包路径转化为文件路径
		URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
		if (url != null) {
			File classpathFile = new File(url.getFile());
			File[] fileList = classpathFile.listFiles();
			if (fileList != null && fileList.length > 0) {
				for (File file : fileList) {
					if (file.isDirectory()) {
						doScanner(packageName+ "." + file.getName());
					} else {
						if (file.getName().endsWith(".class")) {
							classNames.add(packageName + "." + file.getName().replace(".class", ""));
						}
					}
				}
			}
		}
	}

	/**
	 * 实例化扫描的类，并放入IOC容器
	 */
	private void doInstance() {
		if (classNames.isEmpty()) {
			return;
		}
		try {
			// 遍历所有的类，初始化标注有@NsController和@NsService注解的类
			for (String className : classNames) {
				Class<?> clazz = Class.forName(className);
				// 如果是@NsController注解，直接初始化
				if (clazz.isAnnotationPresent(NsController.class)) {
					Object instance = clazz.newInstance();
					String beanName = firstToLowerCase(clazz.getSimpleName());
					ioc.put(beanName, instance);
				} else if (clazz.isAnnotationPresent(NsService.class)) {
					// @NsService注解的类，判断是否有别名
					String beanName = firstToLowerCase(clazz.getSimpleName());
					String serviceValue = clazz.getAnnotation(NsService.class).value();
					if (!"".equals(serviceValue)) {
						beanName = serviceValue;
					}
					Object instance = clazz.newInstance();
					ioc.put(beanName, instance);
					// 获取service对应的所有接口，绑定对应的实例，目前只处理一个接口对应一个实现
					for (Class<?> clazzInterface : clazz.getInterfaces()) {
						if (ioc.containsKey(clazzInterface.getName())) {
							continue;
						}
						// 接口类型当做ioc的key
						ioc.put(clazzInterface.getName(), instance);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 依赖注入
	 * 注入所有使用@NsAutowired注解的字段
	 */
	private void doAutowired() {
		if (ioc.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> entry : ioc.entrySet()) {
			// 拿到所有的字段，包括private、protected等
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				// 如果字段标注了@NsAutowired注解，再继续
				if (!field.isAnnotationPresent(NsAutowired.class)) {
					continue;
				}
				// 此处先按照注入的均为接口，默认取接口全路径
				String beanName = field.getType().getName();
				
				// 如果有自定义名称，使用自定义的
				if (!"".equals(field.getAnnotation(NsAutowired.class).value())) {
					beanName = field.getAnnotation(NsAutowired.class).value();
				}
				// 给字段赋值
				field.setAccessible(true);
				try {
					field.set(entry.getValue(), ioc.get(beanName));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 初始化handlerMapping，生成requestMapping和method的映射关系
	 */
	private void doInitHandlerMapping() {
		if (ioc.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> entry : ioc.entrySet()) {
			Class<?> clazz = entry.getValue().getClass();
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
				handlerList.add(new Handler(entry.getValue(), pattern, method));
			}
		}
	}

	/**
	 * 类名首字母转化为小写
	 * @param className 类型
	 * @return 首字母小写的类名
	 */
	private String firstToLowerCase(String className) {
		char[] chars = className.toCharArray();
		chars[0] += 32;
		return String.valueOf(chars);
	}
}
