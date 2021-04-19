package com.ns.springframework.beans;

/**
 * 定义Bean对象信息
 * @author ns
 * @date 2021/4/14  18:45
 */
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
