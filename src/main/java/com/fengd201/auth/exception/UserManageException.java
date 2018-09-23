package com.fengd201.auth.exception;

public class UserManageException extends Exception {

  private static final long serialVersionUID = 1L;

  private String userName;

  public UserManageException() {
    super();
  }

  public UserManageException(String message) {
    super(message);
  }

  public UserManageException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserManageException(Throwable cause) {
    super(cause);
  }

  public UserManageException(String userName, String message) {
    super(message);
    this.userName = userName;
  }

  public UserManageException(String userName, String message, Throwable cause) {
    super(message, cause);
    this.userName = userName;
  }

  /**
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }
}
