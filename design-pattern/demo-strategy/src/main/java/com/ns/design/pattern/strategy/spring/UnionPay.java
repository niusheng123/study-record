package com.ns.design.pattern.strategy.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author ns
 * @date 2021/4/6  18:26
 */
@Service
@Qualifier("unionPay")
public class UnionPay implements IPay {
	
	@Override
	public void pay() {
		System.out.println("银联支付");	
	}
}
