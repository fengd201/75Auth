package com.fengd201.auth.common.cache;

public interface CacheStrategy {

  public Cacheable get(Object identifier);

  public void put(Cacheable object);

  public boolean remove(Object identifier);

  public boolean contains(Object identifier);
}
