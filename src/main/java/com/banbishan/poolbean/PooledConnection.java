package com.banbishan.poolbean;

import java.sql.Connection;

public class PooledConnection {
	
	private Connection conn;
	
	private boolean isBusy = false;
	
	public PooledConnection(Connection conn, boolean isBusy){
		this.conn = conn;
		this.isBusy = isBusy;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}
	
	public void close(){
		this.isBusy = false;
	}

}
