### 单例模式 ###
##### 定义 #####
~~~~
单例模式是指在任何情况下有且只有一个实例，并且提供一个全局访问点。
~~~~
##### 特点 #####
~~~~
私有化构造器，公共静态方法。
~~~~
#### 饿汉式单例 ####
~~~~
饿汉式单例在类加载的时候就立即初始化，并且创建单例对象，绝对线程安全。
~~~~
+ 写法一
```java
public class HungrySingleton {
	
	private static final HungrySingleton hungrySingleton = new HungrySingleton();
	
	private HungrySingleton() {}
	
	public static HungrySingleton getInstance() {
		return hungrySingleton;
	}
}
```
+ 写法二
```java
public class HungryStaticSingleton {
	
	private static HungryStaticSingleton HUNGRY_SINGLETON;
	
	static {
		HUNGRY_SINGLETON = new HungryStaticSingleton();
	}
	
	private HungryStaticSingleton() {}
	
	public static HungryStaticSingleton getInstance() {
		return HUNGRY_SINGLETON;
	}
}
```
#### 懒汉式单例 ####
~~~~
对象在第一次被调用时才初始化。
~~~~
+ 方式一：以下方式存在线程安全隐患，在单线程环境下可以保证单例，但是在多线程下，可能会创建多个实例对象。
```java
public class LazySingleton {
	
	private static LazySingleton lazySingleton;
	
	private LazySingleton() {}
	
	public static LazySingleton getInstance() {
		if (lazySingleton == null) {
			lazySingleton = new LazySingleton();
		}
		return lazySingleton;
	}
}
```
+ 方式二：利用synchronized关键字解决线程安全问题，但是在并发情况下，所有的线程都被阻塞，会造成性能问题。
```java
public class LazySyncSingleton {
	
	private static LazySyncSingleton lazySyncSingleton;
	
	private LazySyncSingleton() {}
	
	public synchronized static LazySyncSingleton getInstance() {
		if (lazySyncSingleton == null) {
			lazySyncSingleton = new LazySyncSingleton();
		}
		return lazySyncSingleton;
	}
}

```
+ 方法三：双重检查锁创建单例，也是最常用的方式。此方式解决了饿汉式单例资源浪费问题，也解决了synchronized性能问题。
```java
public class DoubleCheckLazySingleton {
	
	private static DoubleCheckLazySingleton doubleCheckLazySingleton;
	
	private DoubleCheckLazySingleton() {}
	
	public static DoubleCheckLazySingleton getInstance() {
		if (doubleCheckLazySingleton == null) {
			synchronized (DoubleCheckLazySingleton.class) {
				if (doubleCheckLazySingleton == null) {
					doubleCheckLazySingleton = new DoubleCheckLazySingleton();
				}
			}
		}
		return doubleCheckLazySingleton;
	}
}
```
####反射破坏单例####
~~~~
以上所有单例写法，即使存在私有化构造器，但是在反射通过构造器创建对象时，还是会创建出新的对象。
~~~~
```java
public class ReflexDamageSingletonTest {

	public static void main(String[] args) throws Exception {
		Class<?> clazz = DoubleCheckLazySingleton.class;
		Constructor constructor = clazz.getDeclaredConstructor();
		constructor.setAccessible(true);
		Object instance1 = constructor.newInstance();
		Object instance2 = constructor.newInstance();
		System.out.println(instance1 == instance2);
	}
}
```
以上反射执行的结果肯定是false，虽然正常开发没人闲的去干这种事，但是为了以防万一，可以通过以下方式来防止别人通过反射破坏单例。利用内部类加载机制，在反射创建对象时，判断内部类是否已经创建好了对象。
```java
public class InnerClassSingleton {
	
	private InnerClassSingleton(){
		if (InnerClass.INNER_CLASS_SINGLETON != null) {
			throw new RuntimeException("不允许创建多个单例对象");
		}
	}
	
	public static InnerClassSingleton getInstance() {
		return InnerClass.INNER_CLASS_SINGLETON;
	}
	
	private static class InnerClass {
		private static final InnerClassSingleton INNER_CLASS_SINGLETON = new InnerClassSingleton();
	}
}
```
#### 枚举式单例 ####
~~~~
枚举式单例是一种不会被反射破坏，不会被序列化破坏的单例模式。
~~~~
