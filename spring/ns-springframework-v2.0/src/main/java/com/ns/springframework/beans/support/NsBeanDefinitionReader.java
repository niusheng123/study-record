package com.ns.springframework.beans.support;

import com.ns.springframework.annotation.NsController;
import com.ns.springframework.annotation.NsService;
import com.ns.springframework.beans.NsBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 解析配置文件，并将相关的类加载成BeanDefinition
 * @author ns
 * @date 2021/4/14  18:46
 */
public class NsBeanDefinitionReader {
	
	private List<String> beanClassNames = new ArrayList<>();

	/**
	 * 配置文件扫描路径对应的key
	 */
	private static final String SCAN_PACKAGE = "componentScan";

	/**
	 * 构造器初始化时，将需要扫描的类都扫描出来
	 * @param locations 包路径集合
	 */
	public NsBeanDefinitionReader(String... locations) {
		Properties config = new Properties();
		if (locations.length > 0) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replaceAll("classpath:", ""));
			try {
				config.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		doScanner(config.getProperty(SCAN_PACKAGE));
	}

	/**
	 * 扫描相关的类
	 * 将以.class结尾的类添加到缓存中
	 */
	private void doScanner(String packageName) {
		// 包路径转化为文件路径
		URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
		if (url != null) {
			File classpathFile = new File(url.getFile());
			File[] fileList = classpathFile.listFiles();
			if (fileList != null && fileList.length > 0) {
				for (File file : fileList) {
					if (file.isDirectory()) {
						doScanner(packageName+ "." + file.getName());
					} else {
						if (file.getName().endsWith(".class")) {
							beanClassNames.add(packageName + "." + file.getName().replace(".class", ""));
						}
					}
				}
			}
		}
	}

	/**
	 * 将扫描到的类转化为NsBeanDefinition对象
	 * @return List<NsBeanDefinition>
	 */
	public List<NsBeanDefinition> loadBeanDefinitions() {
		if (beanClassNames.isEmpty()) {
			return null;
		}
		List<NsBeanDefinition> beanDefinitionList = new ArrayList<>();
		for (String beanClassName : beanClassNames) {
			try {
				Class<?> clazz = Class.forName(beanClassName);
				// 如果类是接口，不做处理，接口通过实现类来创建对象
				if (clazz.isInterface()) {
					continue;
				}
				// 只有添加了@NsController注解或者@NsService注解的类，才会交给容器管理
				if (clazz.isAnnotationPresent(NsController.class) || clazz.isAnnotationPresent(NsService.class)) {
					// 生成类信息
					beanDefinitionList.add(createBeanDefinition(firstToLowerCase(clazz.getSimpleName()),clazz.getName()));
					for (Class<?> clazzInterface : clazz.getInterfaces()) {
						// 接口使用类全路径，对应的实例类用实现类
						beanDefinitionList.add(createBeanDefinition(clazzInterface.getName(),clazz.getName()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return beanDefinitionList;
	}

	/**
	 * 创建NsBeanDefinition对象
	 * @param factoryBeanName 首字母小写的类名
	 * @param beanClassName 包名+类名
	 * @return 类定义对象
	 */
	private NsBeanDefinition createBeanDefinition(String factoryBeanName, String beanClassName) {
		NsBeanDefinition beanDefinition = new NsBeanDefinition();
		beanDefinition.setBeanClassName(beanClassName);
		beanDefinition.setFactoryBeanName(factoryBeanName);
		return beanDefinition;
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
