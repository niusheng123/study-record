### 手写Spring第二版 ###
相对于第一版，主要修改内容：
1. 由BeanDefinitionReader加载配置文件并扫描相关的类
2. 将扫描到的类封装成BeanDefinition对象
3. ApplicationContext实例创建时，实例化对象并依赖注入
4. ApplicationContext实例化对象时，统一调用getBean()方法并封装成BeanWrapper对象，放入真正的ioc容器中。
5. ApplicationContext进行依赖注入时，会判断是否为循环依赖，如果是循环依赖，则递归调用getBean()给循环依赖的对象赋值。
### 完整案例如下 ###
#### pom.xml添加依赖 ####
```xml
<dependency>
	  <groupId>javax.servlet</groupId>
	  <artifactId>servlet-api</artifactId>
	  <version>2.5</version>
  </dependency>
```
#### 添加资源文件application.properties ####
```properties
componentScan=com.ns.web
```
#### 配置web.xml ####
```xml
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xmlns="http://java.sun.com/xml/ns/javaee"
		 xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
		 version="3.0">
	<servlet>
		<servlet-name>nsMvc</servlet-name>
		<servlet-class>com.ns.springframework.servlet.NsDispatcherServlet</servlet-class>
		<init-param>
			<param-name>configLocation</param-name>
			<param-value>classpath:application.properties</param-value>
		</init-param>
	</servlet>

	<servlet-mapping>
		<servlet-name>nsMvc</servlet-name>
		<url-pattern>/*</url-pattern>
	</servlet-mapping>
</web-app>
```
#### 定义各个注解类 ####
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsController {
	
}

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsService {
	
	String value() default "";
}

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsAutowired {
	
	String value() default "";
}

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsRequestMapping {
	
	String value() default "";
}

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsRequestParam {
	
	String value() default "";
	
	boolean required() default true;
}
```

#### Bean定义NsBeanDefinition ####
```java
public class NsBeanDefinition {

	/**
	 * 类对应的全路径，如果是接口，则为实现类的全路径
	 */
	private String beanClassName;

	/**
	 * 类名首字母小写，接口的话为接口全路径
	 */
	private String factoryBeanName;

	public String getBeanClassName() {
		return beanClassName;
	}

	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}

	public String getFactoryBeanName() {
		return factoryBeanName;
	}

	public void setFactoryBeanName(String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}
}
```
#### ioc对象类NsBeanWrapper ####
```java
public class NsBeanWrapper {
	
	private Object wrapperInstance;
	
	private Class<?> wrapperClass;

	public NsBeanWrapper(Object wrapperInstance) {
		this.wrapperInstance = wrapperInstance;
		this.wrapperClass = wrapperInstance.getClass();
	}

	public Object getWrapperInstance() {
		return wrapperInstance;
	}

	public Class<?> getWrapperClass() {
		return wrapperClass;
	}
}
```
#### 配置文件解析类NsBeanDefinitionReader ####
```java
public class NsBeanDefinitionReader {
	
	private List<String> beanClassNames = new ArrayList<>();

	/**
	 * 配置文件扫描路径对应的key
	 */
	private static final String SCAN_PACKAGE = "componentScan";

	/**
	 * 构造器初始化时，将需要扫描的类都扫描出来
	 * @param locations 包路径集合
	 */
	public NsBeanDefinitionReader(String... locations) {
		Properties config = new Properties();
		if (locations.length > 0) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replaceAll("classpath:", ""));
			try {
				config.load(is);
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
		doScanner(config.getProperty(SCAN_PACKAGE));
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
							beanClassNames.add(packageName + "." + file.getName().replace(".class", ""));
						}
					}
				}
			}
		}
	}

	/**
	 * 将扫描到的类转化为NsBeanDefinition对象
	 * @return List<NsBeanDefinition>
	 */
	public List<NsBeanDefinition> loadBeanDefinitions() {
		if (beanClassNames.isEmpty()) {
			return null;
		}
		List<NsBeanDefinition> beanDefinitionList = new ArrayList<>();
		for (String beanClassName : beanClassNames) {
			try {
				Class<?> clazz = Class.forName(beanClassName);
				// 如果类是接口，不做处理，接口通过实现类来创建对象
				if (clazz.isInterface()) {
					continue;
				}
				// 只有添加了@NsController注解或者@NsService注解的类，才会交给容器管理
				if (clazz.isAnnotationPresent(NsController.class) || clazz.isAnnotationPresent(NsService.class)) {
					// 生成类信息
					beanDefinitionList.add(createBeanDefinition(firstToLowerCase(clazz.getSimpleName()),clazz.getName()));
					for (Class<?> clazzInterface : clazz.getInterfaces()) {
						// 接口使用类全路径，对应的实例类用实现类
						beanDefinitionList.add(createBeanDefinition(clazzInterface.getName(),clazz.getName()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return beanDefinitionList;
	}

	/**
	 * 创建NsBeanDefinition对象
	 * @param factoryBeanName 首字母小写的类名
	 * @param beanClassName 包名+类名
	 * @return 类定义对象
	 */
	private NsBeanDefinition createBeanDefinition(String factoryBeanName, String beanClassName) {
		NsBeanDefinition beanDefinition = new NsBeanDefinition();
		beanDefinition.setBeanClassName(beanClassName);
		beanDefinition.setFactoryBeanName(factoryBeanName);
		return beanDefinition;
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
```
#### 应用上下文NsApplicationContext ####
```java
public class NsApplicationContext {

	private final Set<String> singletonsCurrentlyInCreation =
			Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/**
	 * 一级缓存，缓存单例对象，key为beanName，value为bean实例
	 */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
	/**
	 * 二级缓存
	 * 早起单例对象缓存 key为beanName,value为bean实例
	 */
	private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

	/**
	 * 类定义对象，key为beanName，value是类信息
	 */
	private final Map<String, NsBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

	/**
	 * 单例缓存容器，key为类全路径，value为对应的实例
	 */
	private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

	/**
	 * 真正的ioc容器
	 */
	private Map<String, NsBeanWrapper> ioc = new ConcurrentHashMap<>();

	public NsApplicationContext(String... locations) {
		
		// 1. 初始化BeanDefinitionReader，并扫描相关的类
		NsBeanDefinitionReader beanDefinitionReader = new NsBeanDefinitionReader(locations);
		// 2. 将扫描到的类封装成NsBeanDefinition对象，类为类名首字母小写，接口为接口全路径
		List<NsBeanDefinition> beanDefinitionList = beanDefinitionReader.loadBeanDefinitions();
		// 3. 将相关的类放入ioc容器中
		doRegistryBeanDefinition(beanDefinitionList);
		// 4. 依赖注入
		doAutowired();
	}
	
	/**
	 * 将相关的类放入ioc容器
	 * @param beanDefinitionList List<NsBeanDefinition>
	 */
	private void doRegistryBeanDefinition(List<NsBeanDefinition> beanDefinitionList) {
		if (beanDefinitionList.isEmpty()) {
			return;
		}
		for (NsBeanDefinition beanDefinition : beanDefinitionList) {
			if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
				throw new RuntimeException(beanDefinition.getFactoryBeanName() + "is exist！！！");
			}
			beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
		}
	}

	/**
	 * 依赖注入
	 */
	private void doAutowired() {
		for (Map.Entry<String, NsBeanDefinition> definitionEntry : beanDefinitionMap.entrySet()) {
			String beanName = definitionEntry.getKey();
			// getBean()为统一创建Bean的入口
			getBean(beanName);
		}
	}

	/**
	 * 根据类名获取Bean实例
	 * @param clazz 类对象
	 * @return Bean实例
	 */
	public Object getBean(Class<?> clazz) {
		return getBean(clazz.getName());
	}

	/**
	 * 根据beanName后去类实例
	 * @param beanName beanName
	 * @return Bean实例
	 */
	public Object getBean(String beanName) {
		// 先拿到BeanDefinition对象
		NsBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
		if (beanDefinition == null) {
			return null;
		}
		Object singleton = getSingleton(beanName, beanDefinition);
		if (singleton != null) {
			return singleton;
		}
		// 标记bean正在创建
		singletonsCurrentlyInCreation.add(beanName);
		// 1.创建实例对象
		Object instance = initialBean(beanDefinition);
		// 放入一级缓存
		singletonObjects.put(beanName, instance);
		// 2. 封装成BeanWrapper对象
		NsBeanWrapper beanWrapper = new NsBeanWrapper(instance);
		// 3. 将BeanWrapper对象放入ioc容器中
		ioc.put(beanName, beanWrapper);
		// 4. 依赖注入
		populationBean(beanWrapper);
		// 5. 返回Bean实例对象
		return ioc.get(beanName).getWrapperInstance();
	}

	/**
	 * 初始化Bean
	 * @param beanDefinition bean定义对象
	 * @return 实例
	 */
	private Object initialBean(NsBeanDefinition beanDefinition) {
		Object instance = null;
		// 拿到类全路径
		String className = beanDefinition.getBeanClassName();
		// 先从单例缓存中获取，如果缓存没有，再反射创建实例
		if (factoryBeanObjectCache.containsKey(className)) {
			instance = factoryBeanObjectCache.get(className);
		} else {
			try {
				Class<?> clazz = Class.forName(className);
				instance = clazz.newInstance();
				factoryBeanObjectCache.put(className, instance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	/**
	 * 依赖注入
	 * @param beanWrapper beanWrapper对象
	 */
	private void populationBean(NsBeanWrapper beanWrapper) {
		Class<?> clazz = beanWrapper.getWrapperClass();
		// 遍历类中所有字段
		for (Field field : clazz.getDeclaredFields()) {
			// 只有添加了@NsAutowired注解的类才会依赖注入
			if (field.isAnnotationPresent(NsAutowired.class)) {
				// 拿到注解的值
				String autowiredBeanName = field.getAnnotation(NsAutowired.class).value();
				// 如果只为空，判断注入的字段对应Class是否为接口
				if ("".equals(autowiredBeanName)) {
					if (field.getType().isInterface()) {
						// 如果是接口，则beanName为类全路径
						autowiredBeanName = field.getType().getName();
					} else {
						// 不是接口，beanName为类名首字母小写
						autowiredBeanName = firstToLowerCase(field.getType().getSimpleName());
					}
				}
				// 设置强制访问
				field.setAccessible(true);
				// 如果说依赖注入的bean已经实例化了，再注入
				if (ioc.containsKey(autowiredBeanName)) {
					try {
						field.set(beanWrapper.getWrapperInstance(), getBean(autowiredBeanName));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	/**
	 * 获取单例Bean对象
	 * 先从一级缓存中获取
	 * 如果一级缓存没有，但是正在创建的bean中有当前beanName，
	 * @param beanName beanName
	 * @param beanDefinition bean定义
	 * @return 实例
	 */
	private Object getSingleton(String beanName, NsBeanDefinition beanDefinition) {
		Object instance = singletonObjects.get(beanName);
		// 如果一级缓存没有，但是正在创建的bean标识中有，说明是循环依赖
		if (instance == null && singletonsCurrentlyInCreation.contains(beanName)) {
			// 从早期bean缓存中获取
			instance = earlySingletonObjects.get(beanName);
			// 如果二级缓存中没有
			if (instance == null) {
				// 从三级缓存中获取
				instance = initialBean(beanDefinition);
				earlySingletonObjects.put(beanName, instance);
			}
		}
		return instance;
	}

	/**
	 * 获取ioc容器
	 * @return Map<String, NsBeanWrapper>
	 */
	public Map<String, NsBeanWrapper> getIoc() {
		return this.ioc;
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
```
#### 应用入口NsDispatcherServlet ####
```java
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
```
#### 路径与方法映射关系类Handler ####
```java
public class Handler {

	/**
	 * 方法所在的controller的实例
	 */
	protected Object controller;

	/**
	 * 方法对象
	 */
	protected Method method;

	/**
	 * url路径
	 */
	protected Pattern urlPatter;

	/**
	 * 参数顺序
	 */
	protected Map<String,Integer> paramIndexMapping;

	public Handler(Object controller, Pattern urlPatter, Method method) {
		this.controller = controller;
		this.urlPatter = urlPatter;
		this.method = method;
		paramIndexMapping = new HashMap<>(8);
		setParamIndexMapping(method);
	}

	private void setParamIndexMapping(Method method) {

		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
				paramIndexMapping.put(parameterType.getName(), i);
			} else {
				// 参数注解
				Annotation[][] annotations = method.getParameterAnnotations();
				String paramName = (method.getParameters())[i].getName();
				for (Annotation[] value : annotations) {
					for (Annotation annotation : value) {
						if (annotation instanceof NsRequestParam) {
							// 拿到参数别名
							paramName = ((NsRequestParam) annotation).value();
						}
					}
				}
				paramIndexMapping.put(paramName, i);
			}
		}
	}
}
```
