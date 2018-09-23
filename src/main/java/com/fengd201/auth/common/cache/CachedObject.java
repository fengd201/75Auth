package com.fengd201.auth.common.cache;

import java.util.Calendar;
import java.util.Date;

public class CachedObject implements Cacheable {

  private Date expireDate;
  private Object Identifier;
  public Object value;

  public CachedObject(Object value, Object id, int minutesToLive) {
    this.value = value;
    this.Identifier = id;

    if (minutesToLive != 0) {
      expireDate = new Date();
      Calendar cal = Calendar.getInstance();
      cal.setTime(expireDate);
      cal.add(cal.MINUTE, minutesToLive);
      expireDate = cal.getTime();
    }
  }

  @Override
  public boolean isExpired() {
    if (null != expireDate) {
      if (expireDate.before(new Date()))
        return true;
      else
        return false;
    } else {
      return false;
    }
  }

  @Override
  public Object getIndentifier() {
    return Identifier;
  }
}
