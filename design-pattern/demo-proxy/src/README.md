### 代理模式 ###

代理模式是指为目标对象提供一种代理，以控制或增强目标对象。代理对象在客户端和目标对象之间起到中介作用。
### 代理构成 ###
代理模式一版由三种对象构成  
1. 抽象对象，一般是来声明代理对象和被代理对象之间的公共方法，该类可以是接口，也可以是抽象类。  
2. 目标对象，又称为被代理类，是真正执行业务逻辑的类。  
3. 代理对象，又称为代理类，此类中一般包含了目标对象的引用，因此具备了目标对象的代理权。并且可以对目标对象进行逻辑增强或控制性操作。
### 代理分类 ###
> 静态代理  
> 动态代理  
>> JDK动态代理  
>> Cglib动态代理
### 静态代理 ###
- 最大的特点：代理对象直接持有目标对象的引用。  
- 以买饭为例，小王和小张同住一个屋檐下，到了饭点了，小王要去买饭，小张还没起床，但是也想吃，就让小王帮忙带饭回来，此时，小王就属于代理对象，小张属性目标对象，买饭就成了抽象对象。
+ 抽象对象
```java
public interface IBuyLaunch {

	/**
	 * 买饭
	 */
	void buyLaunch();
}
```
+ 目标对象（被代理类）
```java
public class XiaoZhang implements IBuyLaunch{
	
	@Override
	public void buyLaunch() {
		System.out.println("小张：我还没起床，但是想吃饭了");	
	}
}
```
+ 代理对象（代理类）
```java
public class XiaoWang implements IBuyLaunch{
	
	private XiaoZhang xiaoZhang;

	public XiaoWang(XiaoZhang xiaoZhang) {
		this.xiaoZhang = xiaoZhang;
	}

	@Override
	public void buyLaunch() {
		System.out.println("小王：我去买饭");
		// 帮小张去买饭
		xiaoZhang.buyLaunch();
		System.out.println("小王：好，我去帮你买");
	}
}
```
以上场景只适用于一个屋檐下的一两个人，如果一个公司全部人员都要帮忙带饭呢？因此就出现了饿了么、美团此类App，他们负责统一代理需要买饭的人群，此时静态代理已经不能满足此类需要，因此就出现了动态代理。目前最普遍使用的动态代理方式有两种：JDK动态代理和Cglib动态代理。  
### JDK动态代理 ###
#### 原理 ####
+ 通过字节码重组，生成新的对象来代理原来的对象。  
1. 获取目标对象的引用，通过反射获取目标对象实现的所有接口，类加载器。 
2. JDK代理生成新的类，并且新的类要实现原目标类的所有接口。
3. 动态生成Java代码，新的代码中包含增强逻辑等。
4. 编译成.class文件。
5. 加载到JVM虚拟机中运行。
#### 实现示例 ####
+ 抽象对象（接口）
```java
public interface IBuyLaunch {

	/**
	 * 买饭
	 */
	void buyLaunch();
}
```
+ 目标对象（小张和小王）
```java
public class XiaoZhang implements IBuyLaunch {
	
	@Override
	public void buyLaunch() {
		System.out.println("小张：我在大排档点了一份米饭");	
	}
}
public class XiaoWang implements IBuyLaunch {
	
	@Override
	public void buyLaunch() {
		System.out.println("小王：我在路边摊点了一只烤鹅！！！");	
	}
}
```
+ 代理对象（外卖小哥）
```java
public class TakeAwayBoy implements InvocationHandler {
	
	private IBuyLaunch object;
	
	public IBuyLaunch getInstance(IBuyLaunch object) {
		this.object = object;
		Class<?> clazz = object.getClass();
		// 创建代理类 -> 参数1：类加载器 参数2 类实现的接口  参数3：实现了InvocationHandler的类
		return (IBuyLaunch) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		before();
		Object obj = method.invoke(object, args);
		after();
		return obj;
	}

	private void before() {
		System.out.println("外卖小哥：在App上等着接单");
	}

	private void after() {
		System.out.println("外卖小哥：已经接到订单，准备取货");
	}
}
```
+ 测试类
```java
public class TestJdkProxy {

	public static void main(String[] args) {
		TakeAwayBoy takeAwayBoy = new TakeAwayBoy();
		IBuyLaunch buyLaunch = takeAwayBoy.getInstance(new XiaoWang());
		buyLaunch.buyLaunch();
	}
}
```
+ 运行结果
~~~
外卖小哥：在App上等着接单
小王：我在路边摊点了一只烤鹅！！！
外卖小哥：已经接到订单，准备取货
~~~
以上通过JDK动态代理实现，由外卖小哥充当代理类，谁需要帮忙买饭，就传哪个对象进去。
### Cglib动态代理 ###
~~~
目标对象不需要实现任何接口，代理类通过继承目标类达到代理的目的。
~~~
+ 代理类
```java
public class CglibProxy implements MethodInterceptor {
	
	public Object getInstance(Class<?> clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		return enhancer.create();
	}
	
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		before();
		Object obj = methodProxy.invokeSuper(o, objects);
		after();
		return obj;
	}

	private void before() {
		System.out.println("外卖小哥：等着接单");
	}

	private void after() {
		System.out.println("外卖小哥：已接到订单，准备取货");
	}
}
```
+ 目标对象
```java
public class XiaoWang {
	
	public void buyLaunch() {
		System.out.println("小王：到饭点了，饿了，到大排档点碗粥喝~~~");
	}
}
```
+ 测试类和运行结果
```java
public class TestCglibProxy {

	public static void main(String[] args) {
		CglibProxy proxy = new CglibProxy();
		XiaoWang xiaoWang = (XiaoWang) proxy.getInstance(XiaoWang.class);
		xiaoWang.buyLaunch();
	}
}
/*
外卖小哥：等着接单
小王：到饭点了，饿了，到大排档点碗粥喝~~~
外卖小哥：已接到订单，准备取货
*/
```
### Cglib和JDK代理区别 ###
1. JDK动态代理实现了目标类的接口，Cglib继承了目标类。
2. JDK动态代理和Cglib动态代理都是在运行时生成字节码，Cglib生成字节码的逻辑更复杂，生成代理类的效率比JDK低。
3. JDK代理调用代理方法是通过反射机制调用的，Cglib是通过FastClass机制直接调用方法的，因此Cglib执行的效率更高。
### 代理模式优缺点 ###
+ 优点
1. 代理模式能将代理对象和目标对象分离。
2. 在一定程度上降低了系统的耦合，扩展性好。
3. 可以起到保护目标对象的作用。
4. 可以起到增强目标对象的作用。
+ 缺点
1. 请求会先通过代理类，会降低请求效率。
2. 系统复杂度增加。
### Spring中代理选择原则 ###
当类有实现接口时，会自动选择JDK动态代理，当没有实现接口时，会选择Cglib动态代理。  
可以手动配置强制使用Cglib代理，配置文件中添加
```xml
<aop:aspectj-autoproxy proxy-target-class="true"/>
```