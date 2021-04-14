### 适配器模式 ###
~~~
适配器模式又叫变压器模式。它的功能是将一个类的接口编程客户端希望的另一个接口，从而使原来因为接口不匹配的两个类可以正常调用。
~~~
#### 适用范围 ####
~~~
已经存在的类，它的方法和新的需求不一致（或者需要扩展原方法才能满足需求）。
适配器模式主要分为三种：类适配器，对象适配器、接口适配器。
~~~
#### 类适配器 ####
类适配器的原理就是通过继承原目标类并实现原目标类实现的接口，这样就保留了原目标类的本身执行逻辑并可添加新的逻辑。
##### 实现示例 #####
以登录为例，公司部署了登录应用到客户现场，但是客户现场使用的是第三方的门户和登录方式，现在就需要扩展我们自己的登录方式来满足从第三方门户登录后，我们系统的登录入口通过触发某个接口来自动登录，接口参数一致，只是第三方为了安全起见，用了独特的加密方式。（实际上就是单点登录的一种简单实现理念）
+ 原抽象接口
```java
public interface ILogin {

	/**
	 * 登录
	 * @param userName md5用户名
	 * @param password md5密码
	 */
	void login(String userName, String password);
}
```
+ 原实现类
```java
public class NormalLogin implements ILogin {
	
	@Override
	public void login(String userName, String password) {
		System.out.println("正常登录，userName和password是md5加密");	
	}
}
```
+ 第三方适配器
```java
public class ThirdLoginAdapter extends NormalLogin implements ILogin {

	@Override
	public void login(String userName, String password) {
		System.out.println("第三方登录，此时用户名和密码是Aes对称加密");
		System.out.println("加密第三方用户名和密码，重新进行md5加密，调用原登录逻辑");
		super.login(userName, password);
	}
}
```
+ 测试类以及结果
```java
public class ClassAdapterTest {

	public static void main(String[] args) {
		ThirdLoginAdapter thirdLogin = new ThirdLoginAdapter();
		thirdLogin.login("zhangsan", "111");
	}
}
/*
第三方登录，此时用户名和密码是Aes对称加密
加密第三方用户名和密码，重新进行md5加密，调用原登录逻辑
正常登录，userName和password是md5加密
*/
```
以上适配案例在不影响现有逻辑的情况下，增加了对第三方登录的适配方案ThirdLoginAdapter，实现了兼容性。
#### 对象适配器 ####
~~~
对象适配器就是通过组合来实现适配功能。简单的说就是持有目标对象的引用，对原结果进行重新处理。
~~~
##### 使用示例 #####
以上类适配器的场景就不适用于对象适配器，用一个新场景来模拟。  
以贷款场景为例，允许贷款的总金额在每个银行都不一致，每个银行有每个银行的风控规则处理逻辑。
+ 抽象接口
```java
public interface ILoan {

	/**
	 * 贷款
	 * @return 可以贷款的金额
	 */
	int loan();
}
```
+ 原实现方式
```java
public class NormalLoan implements ILoan {
	
	@Override
	public int loan() {
		System.out.println("正常借钱，借多少给多少，可借额度：" + 10000);
		return 10000;
	}
}
```
+ 适配方式
```java
public class ICBCLoanAdapter implements ILoan {
	
	private NormalLoan normalLoan;

	public ICBCLoanAdapter(NormalLoan normalLoan) {
		this.normalLoan = normalLoan;
	}

	@Override
	public int loan() {
		int maxLoanMoney = normalLoan.loan();
		System.out.println("根据风控计算，最多借出30%申请金额，可借额度：" + maxLoanMoney * 0.3);
		return (int) (maxLoanMoney * 0.3);
	}
}
```
+ 测试示例及运行结果
```java
public class ObjectAdapterTest {

	public static void main(String[] args) {
		ICBCLoanAdapter icbcLoan = new ICBCLoanAdapter(new NormalLoan());
		icbcLoan.loan();
	}
}
/*
正常借钱，借多少给多少，可借额度：10000
根据风控计算，最多借出30%申请金额，可借额度：3000.0
 */
```
#### 适配器模式优缺点 ####
+ 优点
1. 提高类的透明性和复用性。
2. 目标类和适配器解耦，提高程序的扩展性。
+ 缺点
1. 过多的使用适配器会造成代码凌乱。
