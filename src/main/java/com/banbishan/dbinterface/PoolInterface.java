package com.banbishan.dbinterface;

import com.banbishan.poolbean.PooledConnection;

public interface PoolInterface {
	
	PooledConnection getConnection();
	
	void createConnection(int count);

}
