### 模板方法模式 ###
~~~
模板方法模式又称模板模式，是值定义一个操作中算法的框架，而将一些步骤交给子类去实现。使得子类再不改变一个算法的结构就可以重新实现该算法的某些步骤。  
通俗的说，模板方法模式就是封装一个固定的流程，流程有多个步骤，具体的步骤可以交给子类去实现。就是抽象和继承的应用
~~~
#### 应用场景 ####
1. 一次性实现一个算法不变的部分，并将可变的行为留给子类去实现。
2. 各子类中公共的行为被提取出来并集中到一个公共的父类中，避免代码重复。
3. 模板方法中一般包含一个钩子方法。
~~~
比如我们正常的找工作过程，投简历->跟boss谈->约面试->面试结束->面试成功->办理入职->上班工作。整个过程实际上是个固定的流程。面试是否成功这个环节实际上就可以是个钩子，如果成功，就办理入职，如果失败，就重新找工作。
~~~
+ 抽象算法类
```java
public abstract class AbstractFindWord {
	
	protected void resume() {
		System.out.println("投简历");
	}
	
	protected void communicateWithBoss() {
		System.out.println("Boss看到简历，跟Boss谈");
	}
	
	protected void appointmentInterview() {
		System.out.println("聊得还可以，约面试");
	}
	
	protected void afterInterview() {
		System.out.println("面试结束");
	}
	
	protected boolean isInterviewSuccess() {
		return true;
	}
	
	protected void entry() {
		System.out.println("办理入职");
	}
	
	protected void startWork() {
		System.out.println("开始上班干活");
	}
	
	protected void findWork() {
		resume();
		communicateWithBoss();
		appointmentInterview();
		afterInterview();
		if (isInterviewSuccess()) {
			System.out.println("面试成功，准备办理入职");
		} else {
			System.out.println("面试失败，继续找工作吧");
			return;
		}
		entry();
		startWork();
	}
}
```
+ 小明找工作-实现了抽象算法，重写了钩子函数
```java
public class XiaoMingFindWork extends AbstractFindWord {

	public XiaoMingFindWork() {
		System.out.println("我是小明，开始找工作");
	}

	@Override
	protected boolean isInterviewSuccess() {
		return false;
	}
}
```
+ 小王找工作，没有重新钩子函数
```java
public class XiaoWangFindWork extends AbstractFindWord {

	public XiaoWangFindWork() {
		System.out.println("我是小王，开始找工作");
	}
}
```
+ 测试类以及结果
```java
public class TestFindWork {

	public static void main(String[] args) {
		XiaoMingFindWork xiaoMing = new XiaoMingFindWork();
		xiaoMing.findWork();
		XiaoWangFindWork xiaoWang = new XiaoWangFindWork();
		xiaoWang.findWork();
	}
}
/*
我是小明，开始找工作
投简历
Boss看到简历，跟Boss谈
聊得还可以，约面试
面试结束
面试失败，继续找工作吧

我是小王，开始找工作
投简历
Boss看到简历，跟Boss谈
聊得还可以，约面试
面试结束
面试成功，准备办理入职
办理入职
开始上班干活
*/
```
#### 真实应用场景 ####
1. 我们最长使用的数据库连接过程
2. Spring类启动时refresh()方法
#### 模板方法模式的优缺点 ####
- 优点  
利用模板方法将相同处理逻辑的代码放到抽象类中，提高代码的复用性。  
可以通过在子类中进行扩展，提高代码的可扩展性。  
- 缺点  
类的数目增加，可读性降低。  
继承本身的缺点，如果父类中增加了新的逻辑，所有子类都要跟着变动。
