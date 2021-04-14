#### 委派模式 ###
~~~
委派模式是一种特殊的静态代理模式，他的基本作用就是负责任务的调用和分配任务。不过代理模式注重过程，而委派模式注重结果。
~~~
#### 应用案例 ####
~~~
以外包公司为例，领导接到客户需求，需要一个Java开发人员临时协助工作，领导此时会把需求给下面的人事主管，人事主管再去筛选并选择合适人员。
~~~
+ 抽象对象，员工
```java
public interface IEmployee {

	/**
	 * 工作
	 */
	void work(String task);
}
```
+ 老大BOSS
```java
public class Boss implements IEmployee {
	
	private Manager manager;

	public Boss(Manager manager) {
		this.manager = manager;
	}

	public void work(String task) {
		System.out.println("我是Boss，现在来了个单子，需要"+ task + "开发人员来处理");
		manager.work(task);
	}
}
```
+ 人事主管Manager
人事主管负责哪些人都是在此类中初始化
```java
public class Manager implements IEmployee {
	
	private static Map<String, IEmployee> employeeMap = new HashMap<String, IEmployee>();
	
	static {
		employeeMap.put("vue", new VueDeveloper());
		employeeMap.put("java", new JavaDeveloper());
	}
	
	public void work(String task) {
		if (!employeeMap.containsKey(task)) {
			System.out.println("我是人力总监，没有能做这个任务的人！！！！");
			return;
		}
		System.out.println("我是人力总监，找到可以做这件事的人");
		employeeMap.get(task).work(task);
	}
}
```
+ Java开发人员JavaDeveloper
```java
public class JavaDeveloper implements IEmployee {
	
	public void work(String task) {
		System.out.println("我是Java开发牛大大，外包干活了");	
	}
}
```
+ Vue开发人员VueDeveloper
```java
public class VueDeveloper implements IEmployee {
	
	public void work(String task) {
		System.out.println("我是Vue开发张大大，封闭开发去咯");
	}
}
```
+ 测试类及结果
```java
public class TestDelegate {

	public static void main(String[] args) {
		Boss boss = new Boss(new Manager());
		boss.work("java");
	}
}
/*
我是Boss，现在来了个单子，需要java来处理
我是人力总监，找到可以做这件事的人
我是Java开发牛大大，外包干活了
*/
```
#### 委派模式在源码中的应用 ####
1. JVM对类的加载采用的是双亲委派机制，一个类在加载时，先委派给自己的父类去加载执行，如果父类加载器还存在父类，则继续向上委派，直到顶层的类加载器。
2. Spring核心DispatcherServlet初始化时扫描所有的标记@Controller注解的类，并缓存相关的请求路径和具体对应的方法名，在我们通过客户端请求时，会将这个请求委派给DispatcherServlet，通过反射调用具体方法然后拿到结果。
#### 委派模式优缺点 ####
+ 优点  
委派模式可以将复杂的任务细化，通过统一管理子任务的完成情况来实现整个任务的跟踪，加快任务执行效率。  
+ 缺点  
委派模式需要根据任务的复杂程度进行不同的改变，在任务比较复杂时，可能需要多重委派才能达到结果，容易造成代码复杂和难理解。
