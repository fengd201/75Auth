package com.fengd201.auth.exception;

public class UserManageRecoverableException extends UserManageException {
  private static final long serialVersionUID = 1L;

  public UserManageRecoverableException() {
    super();
  }

  public UserManageRecoverableException(String message) {
    super(message);
  }

  public UserManageRecoverableException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserManageRecoverableException(Throwable cause) {
    super(cause);
  }

  public UserManageRecoverableException(String userName, String message) {
    super(userName, message);
  }

  public UserManageRecoverableException(String userName, String message, Throwable cause) {
    super(userName, message, cause);
  }
}
