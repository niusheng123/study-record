### Spring从IOC、DI、Aop到Mvc实现思路 ###

+ 配置阶段
   1. 配置web.xml文件		DispatcherServlet
   2. 设置init-param     	    configLocation=classpath:application.properties
   3. 设置url-pattern            /*
   4. 配置Annotation           @Controller、@Service、@RequestMapping、@Autowaired
+ 初始化阶段
   1. 调用DistaptcherServlet init()方法
   2. 加载配置文件
   3. 扫描相关的类
   4. IOC：实例化特定注解的类并保存到IOC容器中（缓存对象）
   5. DI：对特定注解的对象进行依赖注入（反射机制给字段赋值）
   6. MVC：初始化HandlerMapping，将Url和Method进行一对一映射并缓存起来
+ 运行阶段
   1. 调用DispatcherServlet的doGet()/doPost()方法
   2. 从request对象中获得用户输入的url，并找到对应的Method
   3. 利用反射执行方法
   4. 将返回结果返回给客户端
### 实现阶段 ###
#### pom.xml添加依赖 ####
```xml
<dependency>
	<groupId>javax.servlet</groupId>
	<artifactId>servlet-api</artifactId>
	<version>2.5</version>
</dependency>
```
#### resources目录添加配置文件 ####
此处只实现Spring的自动装配，也就是@ComponentScan注解，一次只需要配置好需要扫描的包即可
```properties
componentScan=com.ns.web
```
#### 准备相应的注解类 ####
+ NsController
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsController {
	
}
```
+ NsService
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsService {
	
	String value() default "";
}
```
+ NsAutowired
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsAutowired {
	
	String value() default "";
}
```
+ NsRequestMapping
```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsRequestMapping {
	
	String value() default "";
}
```
+ NsRequestParam
```java
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsRequestParam {
	
	String value() default "";
	
	boolean required() default true;
}
```
#### SpringMvc核心类DispatcherServlet ####
此类实现了HttpServlet，并重写doGet()、doPost()、init()方法，以下为类完整信息，过程请看注释
```java
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
```
+ 路径与方法映射关系类Handler
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
				// 此处可以拿到方法上真正的参数名，前提是在idea settings-build-compiler-Java Compiler 添加Compilation options ，对应的值为-parameters，这样编译阶段就会编译成真正的参数名
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
~~~
注意：Handler类中，想要获取方法参数真正的参数名，需要修改idea配置 settings-build-compiler-Java Compiler ，添加Compilation options ，值为-parameters。
如果不修改，取到的参数名会是args0、args1等
~~~
#### 程序中使用注解 ####
+ 控制层
```java
@NsController
@NsRequestMapping("/test")
public class TestController {
	
	@NsAutowired
	private ITestService testService;
	
	@NsRequestMapping("/add")
	public String add(String type) {
		System.out.println("新增" + type);
		return testService.test(type);
	}
	
	@NsRequestMapping("/delete")
	public void delete(String id) {
		System.out.println("删除成功：" + id);
	}
	
	@NsRequestMapping("/select")
	public List<String> select(String id) {
		System.out.println("查询by" + id );
		return Arrays.asList("aaa","bbb","ccc");
	}
}
```
+ 接口
```java
public interface ITestService {
	
	String test(String type);
}
```
+ 实现类
```java
@NsService
public class TestServiceImpl implements ITestService {

	@Override
	public String test(String type) {
		if ("ceshi".equals(type)) {
			return "测试成功";
		}
		return "滴滴，执行成功";
	}
}
```
#### 结语 ####
~~~
此手写为学习过程记录，非抄袭，只是为了加强个人理解。
努力吧，骚年！！！
~~~