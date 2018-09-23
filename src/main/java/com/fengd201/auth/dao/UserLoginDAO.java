package com.fengd201.auth.dao;

import com.fengd201.auth.modal.entity.UserLoginInfo;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public interface UserLoginDAO {

  /**
   * This is the method to be used to initialize database resources ie. connection.
   * 
   * @param ds
   */
  public void setDataSource(DataSource ds);

  /**
   * This is the method to be used to create a new user
   * 
   * @param loginName
   * @param password
   * @param userType
   * @param roleId
   * @param email
   * @param firstName
   * @param middleName
   * @param lastName
   * @param accountName
   */
  public int create(String loginName, String password, int userType, int roleId, String email,
                    String firstName, String middleName, String lastName, String accountName);

  /**
   * This is the method to be used to get the user record
   * 
   * @param loginName
   * @return UserLoginInfo
   */
  public UserLoginInfo getUserLoginInfo(String loginName);

  /**
   * This is the method to be used to get basic user info including first name, last name, user
   * type, and email address by given login name
   * 
   * @param loginName
   * @return UserLoginInfo
   */
  public UserLoginInfo getBasicUserInfo(String loginName);

  /**
   * This is the method to be used to get basic user info including first name, last name, user
   * type, and email address by given login UUID
   * 
   * @param uuid
   * @return UserLoginInfo
   */
  public UserLoginInfo getBasicUserInfoByUUID(String uuid);

  /**
   * This it the method to be used to get previous user passwords
   * 
   * @param loginName
   * @return List<String>
   * @throws SQLException
   */
  public List<String> getPrevUserPassword(String loginName) throws SQLException;

  /**
   * This is the method to be used to update password
   * 
   * @param loginName
   * @param newPassword
   */
  public void updatePassword(String loginName, String newPassword);

  /**
   * This is the method to be used to update UUID
   * 
   * @param loginName
   * @param uuid
   * @return int
   */
  public int updateUniversallyUniqueId(String loginName, String uuid);

}
