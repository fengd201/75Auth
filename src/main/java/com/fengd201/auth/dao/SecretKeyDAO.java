package com.fengd201.auth.dao;

import java.sql.Timestamp;
import javax.sql.DataSource;

public interface SecretKeyDAO {

  /**
   * This is the method to be used to initialize database resources ie. connection
   * 
   * @param ds
   */
  public void setDataSource(DataSource ds);

  /**
   * This is the method to be used to create new secret key
   * 
   * @param keyStr
   * @param ts
   */
  public void create(String keyStr, Timestamp ts);

  /**
   * This is the method to be used to get secret key
   * 
   * @return String
   */
  public String getSecretKey();
}
