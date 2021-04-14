package com.ns.design.pattern.flyweight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.Vector;

/**
 * @author ns
 * @date 2021/3/30  16:38
 */
public class ConnectionPool {
	
	private static Vector<DatasourceConnection> pool = new Vector<>();
	
	private Integer initPoolSize;
	
	private DatasourceProperty datasourceProperty;

	public ConnectionPool(Integer initPoolSize, DatasourceProperty datasourceProperty) {
		this.initPoolSize = initPoolSize;
		this.datasourceProperty = datasourceProperty;
		init();
	}

	public void init() {
		try {
			Class.forName(datasourceProperty.getDriverClass());
			for (Integer i = 0; i < initPoolSize; i++) {
				DatasourceConnection datasourceConnection = new DatasourceConnection();
				Connection connection = DriverManager.getConnection(datasourceProperty.getJdbcUrl(),datasourceProperty.getUserName(), datasourceProperty.getPassword());
				datasourceConnection.setConnection(connection);
				datasourceConnection.setId(UUID.randomUUID().toString());
				pool.add(datasourceConnection);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static DatasourceConnection getConnection() {
		if (!pool.isEmpty()) {
			for (DatasourceConnection datasourceConnection : pool) {
				if (datasourceConnection.getIsUsed()) {
					continue;
				}
				System.out.println("当前使用的是【" + datasourceConnection.getId() + "】连接对象");
				datasourceConnection.setIsUsed(true);
				return datasourceConnection;
			}
		}
		throw new RuntimeException("连接池未初始化");
	}
	
	
}
