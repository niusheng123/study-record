package com.ns.design.pattern.flyweight;

import lombok.Data;

/**
 * 数据源配置类
 * @author ns
 * @date 2021/3/30  16:41
 */
@Data
public class DatasourceProperty {
	
	private String driverClass;
	
	private String jdbcUrl;
	
	private String userName;
	
	private String password;
}
