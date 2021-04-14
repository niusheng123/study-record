### 享元模式 ###
~~~
享元模式是一种对象池的实现方案。类似于线程池，线程池可以避免不断的创建和销毁对象，消耗性能。其宗旨是共享细粒度对象，将多个同一个对象的访问集中起来。
享元模式把一个对象的状态分为内部状态和外部状态，内部状态是不可变的，外部状态是变化的，然后通过共享不变的部分，达到减少频繁创建对象并节约内存的效果。
享元模式的本质是缓存共享对象，减少内存消耗。
通俗的说，享元模式就是缓存。
~~~
### 应用场景 ###
+ 应用底层的封装。
+ 系统有大量对象，需要缓冲池的场景。  
### 实现案例 ###
以常用的数据库连接池为例（精简模式）
+ 数据库连接信息实体类
```java
@Data
public class DatasourceProperty {
	
	private String driverClass;
	
	private String jdbcUrl;
	
	private String userName;
	
	private String password;
}
```
+ 数据库连接对象
```java
@Data
public class DatasourceConnection {
	
	private Connection connection;
	
	private Boolean isUsed = false;
	
	private String id;

	public void release() {
		System.out.println("释放【"+ this.id + "】对象");
		this.isUsed = false;
	}
}
```
+ 连接池对象
根据配置的初始化大小，在构造时直接创建initPoolSize大小的连接池
```java
public class ConnectionPool {
	
	private static Vector<DatasourceConnection> pool = new Vector<>();
	
	private Integer initPoolSize;
	
	private DatasourceProperty datasourceProperty;

	public ConnectionPool(Integer initPoolSize, DatasourceProperty datasourceProperty) {
		this.initPoolSize = initPoolSize;
		this.datasourceProperty = datasourceProperty;
		init();
	}

	public void init() {
		try {
			Class.forName(datasourceProperty.getDriverClass());
			for (Integer i = 0; i < initPoolSize; i++) {
				DatasourceConnection datasourceConnection = new DatasourceConnection();
				Connection connection = DriverManager.getConnection(datasourceProperty.getJdbcUrl(),datasourceProperty.getUserName(), datasourceProperty.getPassword());
				datasourceConnection.setConnection(connection);
				datasourceConnection.setId(UUID.randomUUID().toString());
				pool.add(datasourceConnection);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static DatasourceConnection getConnection() {
		if (!pool.isEmpty()) {
			for (DatasourceConnection datasourceConnection : pool) {
				if (datasourceConnection.getIsUsed()) {
					continue;
				}
				System.out.println("当前使用的是【" + datasourceConnection.getId() + "】连接对象");
				datasourceConnection.setIsUsed(true);
				return datasourceConnection;
			}
		}
		throw new RuntimeException("连接池未初始化");
	}
	
}
```
+ 测试类
伪测试代码，模拟执行思路
```java
public class TestFlyWeight {

	public static void main(String[] args) {
		DatasourceProperty property = new DatasourceProperty();
		property.setDriverClass("com.mysql.cj.jdbc.Driver");
		property.setJdbcUrl("jdbc:mysql://192.168.0.245:3306/sonar");
		property.setUserName("root");
		property.setPassword("123456");
		// 初始化连接池
		new ConnectionPool(10, property);
		// 获取连接对象1并执行查询
		DatasourceConnection connection = ConnectionPool.getConnection();
		// 获取连接对象2并执行查询
		DatasourceConnection connection2 = ConnectionPool.getConnection();
		// 执行完后释放连接
		connection.release();
		connection2.release();
		DatasourceConnection connection3 = ConnectionPool.getConnection();
	}
}
```
### 源码中使用 ###
#### String中的享元模式 ####
我们最常用的String类为final修饰的，即不可变的。以jdk1.8为例，在以字面量的形式创建String变量时（如：String aa = "hello"，hello即字面量），JVM会在编译期间会在编译期间将该字面量放到字符串常量池中，Java程序启动的时候就已经加载到堆内存中。这个字符串常量池的特点是有且只有一份相同的字面量，如果有相同的字面量，JVM则会返回这个字面量的引用，如果没有相同的字面量，则在这个字符串常量池中创建这个字面量并返回它的引用。
```java
public class TestString {

	public static void main(String[] args) {
		String s1 = "hello";
		String s2 = "hello";
		String s3 = "he" + "llo";
		String s4 = "he" + new String("llo");
		String s5 = new String("hello");
		String s6 = s5.intern();
		String s7 = "he";
		String s8 = "llo";
		String s9 = s7 + s8;
		System.out.println(s1 == s2); 	// true 因为在编译阶段就已经在字符串常量池中创建好 "hello"对象，s1和s2指向同一个引用
		System.out.println(s1 == s3); 	// true 如果是两个字面量拼接，在编译阶段就已经拼接好了
		System.out.println(s1 == s4); 	// false s4 为一个字面量和对象的相加，会生成一个新的对象
		System.out.println(s1 == s9); 	// false s9为两个对象相加，会生成一个新的对象
		System.out.println(s4 == s5); 	// false s4和s5都会生成一个新的对象
		System.out.println(s1 == s6); 	// true s5.intern()会使位于堆中的字符串在运行阶段动态的加入字符串常量池中，如果字符串
										// 常量池中已经有该字面量，则会返回该字面量的引用
	}
}
```
#### Integer中的享元模式 ####
Integer类中有个内部类IntegerCache，会默认初始化-128-127之间的数字，在Integer.valueOf()时，会先从缓存中获取数据，如下
```java
private static class IntegerCache {
	static final int low = -128;
	static final int high;
	static final Integer cache[];

	static {
		// high value may be configured by property
		int h = 127;
		String integerCacheHighPropValue =
			sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
		if (integerCacheHighPropValue != null) {
			try {
				int i = parseInt(integerCacheHighPropValue);
				i = Math.max(i, 127);
				// Maximum array size is Integer.MAX_VALUE
				h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
			} catch( NumberFormatException nfe) {
				// If the property cannot be parsed into an int, ignore it.
			}
		}
		high = h;

		cache = new Integer[(high - low) + 1];
		int j = low;
		for(int k = 0; k < cache.length; k++)
			cache[k] = new Integer(j++);

		// range [-128, 127] must be interned (JLS7 5.1.7)
		assert IntegerCache.high >= 127;
	}

	private IntegerCache() {}
}

public static Integer valueOf(int i) {
	if (i >= IntegerCache.low && i <= IntegerCache.high)
		return IntegerCache.cache[i + (-IntegerCache.low)];
	return new Integer(i);
}
```
测试类
```java
public class TestInteger {

	public static void main(String[] args) {
		Integer a = Integer.valueOf(100);
		Integer b = 100;
		Integer c = Integer.valueOf(1000);
		Integer d = 1000;
		System.out.println(a == b);		// true
		System.out.println(c == d);		// false
	}
}
```
由测试结果可以看出，当值范围不在-128-127之间时，会重新new对象，因此在进行引用比较时，比较结果为false。
### 享元模式优缺点 ###
+ 优点  
减少对象的创建，降低系统的内存，提高效率。  
减少内存之外的其他资源占用。
+ 缺点  
关注内部状态和外部状态，需要考虑线程安全问题。  
复杂化系统逻辑。

