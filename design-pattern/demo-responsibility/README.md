### 责任链模式 ###
~~~
责任链模式是将链中的每个节点看作是一个对象，每个节点处理的请求各不相同，且内部自动维护下一节点对象。
当一个请求从链的首端发起时，会沿着链的路径依次传递给每一个节点对象，直至有一个对象处理这个请求为止。
~~~
#### 适用场景 ####
责任链模式主要是解耦了请求和处理，客户只需将请求发送到链上即可，无需关注请求的内容和处理细节。
1. 多个对象可以处理同一个请求，具体由哪个对象处理则在运行时动态决定。
2. 在不明确指定接受者的情况下，向多个对象中的一个提交一个请求。
3. 可动态指定一组对象处理请求。
#### 使用案例 ####
以登录为例，正常我们登录会验证用户名密码、验证码、用户是否为黑名单、获取用户对应的权限菜单等。我们可以将这一系列过程抽象成几部分。
+ 实体对象User  
```java
@Data
@AllArgsConstructor
public class User {

	private String username;
	
	private String password;
	
	private String captcha;

}
```
+ 抽象链路AbstractHandler（建造者模式构造链路）
```java
public abstract class AbstractHandler<T> {
	
	protected AbstractHandler chain;
	
	public void next(AbstractHandler handler) {
		this.chain = handler;
	}
	
	public abstract void doHandler(User user);
	
	public static class Builder<T> {
		
		private AbstractHandler<T> head;
		
		private AbstractHandler<T> tail;
		
		public Builder<T> addHandler(AbstractHandler<T> handler) {
			if (this.head == null) {
				this.head = this.tail = handler;
				return this;
			}
			this.tail.next(handler);
			this.tail = handler;
			return this;
		}
		
		public AbstractHandler<T> builder() {
			return this.head;
		}
	}
}
```
+ 链路实现类（用户名密码认证、验证码校验、黑名单校验、获取菜单数据）
```java
public class ValidatePassword extends AbstractHandler {

	/**
	 * 密码，就当是从数据库查询出来的
	 */
	private static final String PASSWORD = "123456";
	
	
	@Override
	public void doHandler(User user) {
		if (user == null || StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			System.out.println("用户名和密码不能为空");
			return;
		}
		if (!PASSWORD.equals(user.getPassword())) {
			System.out.println("密码错误");
			return;
		}
		System.out.println("用户名密码验证通过");
		chain.doHandler(user);
	}
}
public class ValidateCaptcha extends AbstractHandler {
	
	private static final String CAPTCHA = "qwer";

	@Override
	public void doHandler(User user) {
		if (StringUtils.isBlank(user.getCaptcha()) || !CAPTCHA.equals(user.getCaptcha())) {
			System.out.println("验证码错误");
			return;
		}
		System.out.println("验证码验证通过");
		chain.doHandler(user);
	}
}
public class ValidateBlackUser extends AbstractHandler {
	
	private static final String BLACK_USER = "xiaowang";
	
	@Override
	public void doHandler(User user) {
		if (BLACK_USER.equals(user.getUsername())) {
			System.out.println("黑名单校验未通过");
			return;
		}
		System.out.println("黑名单验证通过");
		chain.doHandler(user);
	}
}
public class MenuService extends AbstractHandler {
	
	private static final List<String> MENU_LIST = Arrays.asList("用户菜单","角色菜单","部门菜单");
	
	@Override
	public void doHandler(User user) {
		System.out.println("根据用户" +user.getUsername()+ "获取菜单->" + MENU_LIST);	
	}
}
```
+ 链路创建层以及提供的调用接口
```java
public class RequestHandler {
	
	private static AbstractHandler abstractHandler;
	
	static {
		AbstractHandler.Builder builder = new AbstractHandler.Builder();
		builder.addHandler(new ValidatePassword())
				.addHandler(new ValidateCaptcha())
				.addHandler(new ValidateBlackUser())
				.addHandler(new MenuService());
		abstractHandler = builder.builder();
	}
	
	public static void doRequest(User user) {
		abstractHandler.doHandler(user);
	}
}
```
+ 测试类及调用结果
```java
public class TestResponsibility {

	public static void main(String[] args) {
		User user = new User("lisi", "123456", "qwer");
		RequestHandler.doRequest(user);
	}
}
/*
 用户名密码验证通过
 验证码验证通过
 黑名单验证通过
 根据用户lisi获取菜单->[用户菜单, 角色菜单, 部门菜单] 
*/
```
#### 责任链模式在源码中应用 ####
最常见的就是Filter过滤器，将所有实现了Filter接口的类放到一个List,按顺序进行调用。
#### 责任链模式的优缺点 ####
+ 优点  
将请求与处理解耦。  
每个处理节点只关注自己需要处理的逻辑。  
具备链式传递处理请求功能，请求发送者无需知晓链路结构，只需等待处理结果即可。  
链路结构灵活，可以通过改变链路动态的新增或增减责任。  
易于扩展新的请求处理节点。  
+ 缺点  
责任链太长或链路处理时间太长，会影响整体性能。  
如果节点对象循环引用，可能会造成死循环。