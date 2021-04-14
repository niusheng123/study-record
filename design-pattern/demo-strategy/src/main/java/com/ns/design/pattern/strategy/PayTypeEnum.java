package com.ns.design.pattern.strategy;

/**
 * @author ns
 * @date 2021/4/6  18:21
 */
public enum PayTypeEnum {
	WE_CHAT_PAY("weChat", "微信支付"),
	ALI_PAY("aliPay", "支付宝支付"),
	UNION_PAY("unionPay", "银联支付");

	private String code;
	
	private String name;
	
	PayTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
