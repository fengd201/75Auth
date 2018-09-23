package com.fengd201.auth.common.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache implements CacheStrategy {

  class CacheNode {
    CacheNode prev;
    CacheNode next;
    Object identifier;
    Cacheable value;

    public CacheNode(Object identifier, Cacheable value) {
      this.identifier = identifier;
      this.value = value;
    }
  }

  private CacheNode head;
  private CacheNode tail;
  private int capacity;
  private Map<Object, CacheNode> cacheMap;

  public LRUCache(int capacity) {
    this.capacity = capacity;
    this.head = new CacheNode(null, null);
    this.tail = new CacheNode(null, null);
    this.cacheMap = new HashMap<>();
    head.next = tail;
    tail.prev = head;
  }

  @Override
  public Cacheable get(Object identifier) {
    if (contains(identifier)) {
      unlinkNode(identifier);
      moveToTail(identifier);
      return cacheMap.get(identifier).value;
    } else {
      return null;
    }
  }

  @Override
  public void put(Cacheable object) {
    Cacheable obj = get(object.getIndentifier());
    if (null != obj) {
      obj = object;
    } else {
      if (cacheMap.size() >= capacity && capacity > 0)
        remove(head.next.identifier);
      CacheNode node = new CacheNode(object.getIndentifier(), object);
      cacheMap.put(object.getIndentifier(), node);
      moveToTail(object.getIndentifier());
    }
  }

  @Override
  public boolean remove(Object identifier) {
    if (contains(identifier)) {
      unlinkNode(identifier);
      cacheMap.remove(identifier);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean contains(Object identifier) {
    return cacheMap.containsKey(identifier);
  }

  private void unlinkNode(Object identifier) {
    CacheNode node = cacheMap.get(identifier);
    node.prev.next = node.next;
    node.next.prev = node.prev;
    node.next = null;
    node.prev = null;
  }

  private void moveToTail(Object identifier) {
    CacheNode node = cacheMap.get(identifier);
    node.prev = tail.prev;
    node.next = tail;
    tail.prev.next = node;
    tail.prev = node;
  }
}
