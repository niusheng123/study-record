package com.ns.design.pattern.strategy.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author ns
 * @date 2021/4/6  18:25
 */
@Service
@Qualifier("weChat")
public class WeChatPay implements IPay {

	@Override
	public void pay() {
		System.out.println("微信支付");
	}
}
