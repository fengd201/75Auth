package com.fengd201.auth.exception;

public class UserManageSystemException extends UserManageException {

  private static final long serialVersionUID = 1L;

  public UserManageSystemException() {
    super();
  }

  public UserManageSystemException(String message) {
    super(message);
  }

  public UserManageSystemException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserManageSystemException(Throwable cause) {
    super(cause);
  }

  public UserManageSystemException(String userName, String message) {
    super(userName, message);
  }

  public UserManageSystemException(String userName, String message, Throwable cause) {
    super(userName, message, cause);
  }
}
