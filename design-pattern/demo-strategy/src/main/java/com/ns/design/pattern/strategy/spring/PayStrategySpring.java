package com.ns.design.pattern.strategy.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ns
 * @date 2021/4/6  18:42
 */
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
