package com.fengd201.auth.modal.entity;

import java.util.List;

public class UserLoginInfo {
  private int loginCode;
  private List<Integer> accessRolesList;
  private int userId;
  private String loginName;
  private int userType;
  private String firstName;
  private String middleName;
  private String lastName;
  private String accountName;
  private String email;
  private String password;
  private boolean resetFlg;

  /**
   * @return the loginCode
   */
  public int getLoginCode() {
    return loginCode;
  }

  /**
   * @param loginCode
   *          the loginCode to set
   */
  public void setLoginCode(int loginCode) {
    this.loginCode = loginCode;
  }

  /**
   * @return the accessRolesList
   */
  public List<Integer> getAccessRolesList() {
    return accessRolesList;
  }

  /**
   * @param accessRolesList
   *          the accessRolesList to set
   */
  public void setAccessRolesList(List<Integer> accessRolesList) {
    this.accessRolesList = accessRolesList;
  }

  /**
   * @return the userId
   */
  public int getUserId() {
    return userId;
  }

  /**
   * @param userId
   *          the userId to set
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }

  /**
   * @return the loginName
   */
  public String getLoginName() {
    return loginName;
  }

  /**
   * @param loginName
   *          the loginName to set
   */
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  /**
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @param firstName
   *          the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return the middleName
   */
  public String getMiddleName() {
    return middleName;
  }

  /**
   * @param middleName
   *          the middleName to set
   */
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @param lastName
   *          the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email
   *          the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return the accountName
   */
  public String getAccountName() {
    return accountName;
  }

  /**
   * @param accountName
   *          the accountName to set
   */
  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  /**
   * @return the resetFlg
   */
  public boolean isResetFlg() {
    return resetFlg;
  }

  /**
   * @param resetFlg
   *          the resetFlg to set
   */
  public void setResetFlg(boolean resetFlg) {
    this.resetFlg = resetFlg;
  }

  /**
   * @return the userType
   */
  public int getUserType() {
    return userType;
  }

  /**
   * @return the {@link #password}
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password
   *          the {@link #password} to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @param userType
   *          the userType to set
   */
  public void setUserType(int userType) {
    this.userType = userType;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("{loginCode: " + getLoginCode() + ", userId: "
        + getUserId() + ", loginName: " + getLoginName() + ", userType: " + getUserType()
        + ", firstName: " + getFirstName() + ", middleName: " + getMiddleName() + ", lastName: "
        + getLastName() + ", accountName: " + getAccountName() + ", email: " + getEmail()
        + ", resetFlg: " + isResetFlg() + ", accessRolesList: [");
    if (null != getAccessRolesList()) {
      for (int i = 0; i < getAccessRolesList().size(); i++) {
        sb.append(getAccessRolesList().get(i));
        if (i != getAccessRolesList().size() - 1)
          sb.append(", ");
      }
    }
    sb.append("]}");
    return sb.toString();
  }
}
