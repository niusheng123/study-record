### 策略模式 ###
~~~
策略模式是将定义的算法家族，分别封装起来，让它们可以互相替换，从而让算法的变化不会影响到使用算法的用户。
策略模式的本质就是面向对象继承和多态机制，满足同一方法在不同场景下不同的实现。解决多if else 的复杂情况。
~~~
#### 应用场景 ####
1. 针对同一个产品，有多种类型，并且每种类型都有自身独特的实现。
2. 算法需要自由切换
3. 需要屏蔽算法规则。
#### 使用案例 ####
以淘宝购物付款为例，有通过微信支付、支付宝支付、银联支付等，并且每种支付的实现方式都不同。
+ 抽象支付接口
```java
public interface IPay {

	/**
	 * 支付
	 */
	void pay();
}
```
+ 支付枚举类
```java
public enum PayTypeEnum {
	WE_CHAT_PAY("weChat", "微信支付"),
	ALI_PAY("aliPay", "支付宝支付"),
	UNION_PAY("unionPay", "银联支付");

	private String code;
	
	private String name;
	
	PayTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
```
+ 支付实现类
```java
public class WeChatPay implements IPay {

	@Override
	public void pay() {
		System.out.println("微信支付");
	}
}
public class AliPay implements IPay{
	
	@Override
	public void pay() {
		System.out.println("支付宝支付");	
	}
}
public class UnionPay implements IPay {
	
	@Override
	public void pay() {
		System.out.println("银联支付");	
	}
}
```
+ 策略类
```java
public class PayStrategy {
	
	private static Map<String, IPay> payMap = new HashMap<>(4);

	private PayStrategy() {
	}

	static {
		payMap.put(PayTypeEnum.WE_CHAT_PAY.getCode(), new WeChatPay());
		payMap.put(PayTypeEnum.ALI_PAY.getCode(), new AliPay());
		payMap.put(PayTypeEnum.UNION_PAY.getCode(), new UnionPay());
	}
	
	public static IPay getInstance(String type) {
		if (payMap.containsKey(type)) {
			return payMap.get(type);
		}
		throw new RuntimeException("此支付方式正在开发ing");
	}
}
```
+ 测试类及执行结果
```java
public class TestStrategy {

	public static void main(String[] args) {
		PayStrategy.getInstance("weChat").pay();
		PayStrategy.getInstance("aliPay").pay();
		PayStrategy.getInstance("unionPay").pay();
	}
}
/*
 微信支付
 支付宝支付
 银联支付
*/
```
#### Spring中使用策略模式 ####
~~~
在Spring中，Spring帮我们管理了Bean的创建和销毁，策略如何使用呢？
利用Spring的@Qualifier("")注解
~~~
+ 抽象接口
```java
public interface IPay {

	/**
	 * 支付
	 */
	void pay();
}
```
+ 实现类
```java
@Service
@Qualifier("weChat")
public class WeChatPay implements IPay {

	@Override
	public void pay() {
		System.out.println("微信支付");
	}
}
@Service
@Qualifier("aliPay")
public class AliPay implements IPay{
	
	@Override
	public void pay() {
		System.out.println("支付宝支付");	
	}
}
@Service
@Qualifier("unionPay")
public class UnionPay implements IPay {
	
	@Override
	public void pay() {
		System.out.println("银联支付");	
	}
}
```
+ 策略类  
直接利用Spring的@Autowired机制，配合实现类的@Qualifier注解，将多个实现类抽象成一个map
```java
@Service
public class PayStrategySpring {
	
	@Autowired
	private Map<String, IPay> payMap = new HashMap<>(4);
	
	public IPay getInstance(String type) {
		if (payMap.containsKey(type)) {
			return payMap.get(type);
		}
		throw new RuntimeException("功能正在开发ing");
	}
}
```
+ Controller调用
```java
@RestController
public class PayController {
	
	@Autowired
	private PayStrategySpring payStrategySpring;
	
	@GetMapping("/test")
	public void test(@RequestParam String type) {
		payStrategySpring.getInstance(type).pay();
	}
}
```
浏览器输入http://localhost:8089/test?type=aliPay，即可自动执行想要的实现类。
#### 策略模式优缺点 ####
+ 优点  
策略模式符合开闭原则。  
避免过多使用if else。  
使用策略模式可以提高算法的保密性和安全性。  
+ 缺点  
代码会增加过多的类，增加维护难度。  