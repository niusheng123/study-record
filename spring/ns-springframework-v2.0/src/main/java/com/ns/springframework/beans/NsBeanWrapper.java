package com.ns.springframework.beans;

/**
 * @author ns
 * @date 2021/4/14  18:46
 */
public class NsBeanWrapper {
	
	private Object wrapperInstance;
	
	private Class<?> wrapperClass;

	public NsBeanWrapper(Object wrapperInstance) {
		this.wrapperInstance = wrapperInstance;
		this.wrapperClass = wrapperInstance.getClass();
	}

	public Object getWrapperInstance() {
		return wrapperInstance;
	}

	public Class<?> getWrapperClass() {
		return wrapperClass;
	}
}
