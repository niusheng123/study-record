package com.ns.springframework.context;

import com.ns.springframework.annotation.NsAutowired;
import com.ns.springframework.beans.NsBeanDefinition;
import com.ns.springframework.beans.NsBeanWrapper;
import com.ns.springframework.beans.support.NsBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ns
 * @date 2021/4/14  18:46
 */
public class NsApplicationContext {

	private final Set<String> singletonsCurrentlyInCreation =
			Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/**
	 * 一级缓存，缓存单例对象，key为beanName，value为bean实例
	 */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
	/**
	 * 二级缓存
	 * 早起单例对象缓存 key为beanName,value为bean实例
	 */
	private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

	/**
	 * 类定义对象，key为beanName，value是类信息
	 */
	private final Map<String, NsBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

	/**
	 * 单例缓存容器，key为类全路径，value为对应的实例
	 */
	private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

	/**
	 * 真正的ioc容器
	 */
	private Map<String, NsBeanWrapper> ioc = new ConcurrentHashMap<>();

	public NsApplicationContext(String... locations) {
		
		// 1. 初始化BeanDefinitionReader，并扫描相关的类
		NsBeanDefinitionReader beanDefinitionReader = new NsBeanDefinitionReader(locations);
		// 2. 将扫描到的类封装成NsBeanDefinition对象，类为类名首字母小写，接口为接口全路径
		List<NsBeanDefinition> beanDefinitionList = beanDefinitionReader.loadBeanDefinitions();
		// 3. 将相关的类放入ioc容器中
		doRegistryBeanDefinition(beanDefinitionList);
		// 4. 依赖注入
		doAutowired();
	}
	
	/**
	 * 将相关的类放入ioc容器
	 * @param beanDefinitionList List<NsBeanDefinition>
	 */
	private void doRegistryBeanDefinition(List<NsBeanDefinition> beanDefinitionList) {
		if (beanDefinitionList.isEmpty()) {
			return;
		}
		for (NsBeanDefinition beanDefinition : beanDefinitionList) {
			if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
				throw new RuntimeException(beanDefinition.getFactoryBeanName() + "is exist！！！");
			}
			beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
		}
	}

	/**
	 * 依赖注入
	 */
	private void doAutowired() {
		for (Map.Entry<String, NsBeanDefinition> definitionEntry : beanDefinitionMap.entrySet()) {
			String beanName = definitionEntry.getKey();
			// getBean()为统一创建Bean的入口
			getBean(beanName);
		}
	}

	/**
	 * 根据类名获取Bean实例
	 * @param clazz 类对象
	 * @return Bean实例
	 */
	public Object getBean(Class<?> clazz) {
		return getBean(clazz.getName());
	}

	/**
	 * 根据beanName后去类实例
	 * @param beanName beanName
	 * @return Bean实例
	 */
	public Object getBean(String beanName) {
		// 先拿到BeanDefinition对象
		NsBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
		if (beanDefinition == null) {
			return null;
		}
		Object singleton = getSingleton(beanName, beanDefinition);
		if (singleton != null) {
			return singleton;
		}
		// 标记bean正在创建
		singletonsCurrentlyInCreation.add(beanName);
		// 1.创建实例对象
		Object instance = initialBean(beanDefinition);
		// 放入一级缓存
		singletonObjects.put(beanName, instance);
		// 2. 封装成BeanWrapper对象
		NsBeanWrapper beanWrapper = new NsBeanWrapper(instance);
		// 3. 将BeanWrapper对象放入ioc容器中
		ioc.put(beanName, beanWrapper);
		// 4. 依赖注入
		populationBean(beanWrapper);
		// 5. 返回Bean实例对象
		return ioc.get(beanName).getWrapperInstance();
	}

	/**
	 * 初始化Bean
	 * @param beanDefinition bean定义对象
	 * @return 实例
	 */
	private Object initialBean(NsBeanDefinition beanDefinition) {
		Object instance = null;
		// 拿到类全路径
		String className = beanDefinition.getBeanClassName();
		// 先从单例缓存中获取，如果缓存没有，再反射创建实例
		if (factoryBeanObjectCache.containsKey(className)) {
			instance = factoryBeanObjectCache.get(className);
		} else {
			try {
				Class<?> clazz = Class.forName(className);
				instance = clazz.newInstance();
				factoryBeanObjectCache.put(className, instance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	/**
	 * 依赖注入
	 * @param beanWrapper beanWrapper对象
	 */
	private void populationBean(NsBeanWrapper beanWrapper) {
		Class<?> clazz = beanWrapper.getWrapperClass();
		// 遍历类中所有字段
		for (Field field : clazz.getDeclaredFields()) {
			// 只有添加了@NsAutowired注解的类才会依赖注入
			if (field.isAnnotationPresent(NsAutowired.class)) {
				// 拿到注解的值
				String autowiredBeanName = field.getAnnotation(NsAutowired.class).value();
				// 如果只为空，判断注入的字段对应Class是否为接口
				if ("".equals(autowiredBeanName)) {
					if (field.getType().isInterface()) {
						// 如果是接口，则beanName为类全路径
						autowiredBeanName = field.getType().getName();
					} else {
						// 不是接口，beanName为类名首字母小写
						autowiredBeanName = firstToLowerCase(field.getType().getSimpleName());
					}
				}
				// 设置强制访问
				field.setAccessible(true);
				// 如果说依赖注入的bean已经实例化了，再注入
				if (ioc.containsKey(autowiredBeanName)) {
					try {
						field.set(beanWrapper.getWrapperInstance(), getBean(autowiredBeanName));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	/**
	 * 获取单例Bean对象
	 * 先从一级缓存中获取
	 * 如果一级缓存没有，但是正在创建的bean中有当前beanName，
	 * @param beanName beanName
	 * @param beanDefinition bean定义
	 * @return 实例
	 */
	private Object getSingleton(String beanName, NsBeanDefinition beanDefinition) {
		Object instance = singletonObjects.get(beanName);
		// 如果一级缓存没有，但是正在创建的bean标识中有，说明是循环依赖
		if (instance == null && singletonsCurrentlyInCreation.contains(beanName)) {
			// 从早期bean缓存中获取
			instance = earlySingletonObjects.get(beanName);
			// 如果二级缓存中没有
			if (instance == null) {
				// 从三级缓存中获取
				instance = initialBean(beanDefinition);
				earlySingletonObjects.put(beanName, instance);
			}
		}
		return instance;
	}

	/**
	 * 获取ioc容器
	 * @return Map<String, NsBeanWrapper>
	 */
	public Map<String, NsBeanWrapper> getIoc() {
		return this.ioc;
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
