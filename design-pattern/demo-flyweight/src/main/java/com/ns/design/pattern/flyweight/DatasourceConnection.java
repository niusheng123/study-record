package com.ns.design.pattern.flyweight;

import lombok.Builder;
import lombok.Data;

import java.sql.Connection;

/**
 * @author ns
 * @date 2021/3/30  16:40
 */
@Data
public class DatasourceConnection {
	
	private Connection connection;
	
	private Boolean isUsed = false;
	
	private String id;

	public void release() {
		System.out.println("释放【"+ this.id + "】对象");
		this.isUsed = false;
	}
}
