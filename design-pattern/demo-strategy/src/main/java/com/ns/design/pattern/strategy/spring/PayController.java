package com.ns.design.pattern.strategy.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ns
 * @date 2021/4/6  18:44
 */
@RestController
public class PayController {
	
	@Autowired
	private PayStrategySpring payStrategySpring;
	
	@GetMapping("/test")
	public void test(@RequestParam String type) {
		payStrategySpring.getInstance(type).pay();
	}
}
