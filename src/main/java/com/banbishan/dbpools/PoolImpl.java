package com.banbishan.dbpools;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import javax.management.RuntimeErrorException;

import com.banbishan.dbinterface.PoolInterface;
import com.banbishan.poolbean.PooledConnection;

public class PoolImpl implements PoolInterface {
	
	private static String jdbcDriver = "";
	private static String jdbcurl = "";
	private static String userName = "";
	private static String password = "";
	//初始化连接数
	private static int initCount;
	//每次创建多少个对象
	private static int stepSize;
	private static int poolMaxSize;
	//连接对象
	private static Vector<PooledConnection> poolConn = new Vector<PooledConnection>();
	
	
	public PoolImpl(){
		init();
	}
	
	public void init(){
		InputStream in = PoolInterface.class.getClassLoader().getResourceAsStream("config/jdbcPool.properties");
		Properties pro = new Properties();
		try {
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		jdbcDriver = pro.getProperty("jdbcDriver");
		jdbcurl = pro.getProperty("jdbcurl");
		userName = pro.getProperty("userName");
		password = pro.getProperty("password");
		initCount = Integer.valueOf(pro.getProperty("initCount"));
		stepSize = Integer.valueOf(pro.getProperty("stepSize"));
		poolMaxSize = Integer.valueOf(pro.getProperty("poolMaxSize"));
		
		try {
			Driver driver = (Driver)Class.forName(jdbcDriver).newInstance();
			DriverManager.registerDriver(driver);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		createConnection(initCount);
	}

	public PooledConnection getConnection() {
		if (poolConn.size() <= 0) {
			throw new RuntimeException("没有对象可以获取，获取连接对象失败");
		}
		PooledConnection connection = getRealConnection();
		while (connection==null) {
			createConnection(stepSize);
			getRealConnection();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}
	
	private synchronized PooledConnection getRealConnection(){
		for (PooledConnection conn: poolConn) {
			if (!conn.isBusy()) {
				Connection connection = conn.getConn();
				try {
					if (connection.isValid(3000)) {
						Connection validConn = DriverManager.getConnection(jdbcurl, userName, password);
						conn.setConn(validConn);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				conn.setBusy(true);
				return conn;
			}
		}
		return null;
	}

	public void createConnection(int count) {
		for (int i = 0; i < count; i++) {
			if (poolMaxSize > 0 && poolConn.size()+count > poolMaxSize) {
				throw new RuntimeErrorException(null, "超过创建上限，创建数据库连接池失败！");
			}
			try {
				Connection conn = DriverManager.getConnection(jdbcurl, userName, password);
				PooledConnection pooledConn = new PooledConnection(conn,false);
				poolConn.add(pooledConn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

}
