package com.ns.design.pattern.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ns
 * @date 2021/4/6  18:27
 */
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
