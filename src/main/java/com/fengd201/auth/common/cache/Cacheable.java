package com.fengd201.auth.common.cache;

public interface Cacheable {
	
	/**
	 * Check if cache object is expired.
	 * 
	 * Need to be implemented with your own expiration strategy.
	 */
	public boolean isExpired();
	
	/**
	 * @return identifier
	 */
	public Object getIndentifier();
}
