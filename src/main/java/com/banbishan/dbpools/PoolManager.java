package com.banbishan.dbpools;

public class PoolManager {
	private static class createPool{
		private static PoolImpl poolImpl = new PoolImpl();
	}

	public static PoolImpl getPoolImpl(){
		return createPool.poolImpl;
	}
}
