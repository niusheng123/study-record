package com.ns.design.pattern.flyweight;

/**
 * @author ns
 * @date 2021/3/30  17:19
 */
public class TestFlyWeight {

	public static void main(String[] args) {
		DatasourceProperty property = new DatasourceProperty();
		property.setDriverClass("com.mysql.cj.jdbc.Driver");
		property.setJdbcUrl("jdbc:mysql://192.168.0.245:3306/sonar");
		property.setUserName("root");
		property.setPassword("123456");
		// 初始化连接池
		new ConnectionPool(10, property);
		// 获取连接对象1并执行查询
		DatasourceConnection connection = ConnectionPool.getConnection();
		// 获取连接对象2并执行查询
		DatasourceConnection connection2 = ConnectionPool.getConnection();
		// 执行完后释放连接
		connection.release();
		connection2.release();
		DatasourceConnection connection3 = ConnectionPool.getConnection();
	}
}
