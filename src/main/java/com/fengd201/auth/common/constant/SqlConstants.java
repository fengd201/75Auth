package com.fengd201.auth.common.constant;

public class SqlConstants {

  public static final String AUTH_COMMON_LOGIN = "auth_common_login";
  public static final String CREATE_NEW_USER_PROCEDURE = "create_new_user";
  public static final String FETCH_PASS_AND_ACCESS_ROLE_PROCEDURE = AUTH_COMMON_LOGIN
      + ".fetch_pass_and_access_role";
  public static final String FETCH_PREVIOUS_PASSWORD_PROCEDURE = AUTH_COMMON_LOGIN
      + ".fetch_previous_password";
  public static final String RESET_USER_PASSWORD_PROCEDURE = "reset_user_password";
  public static final String USER_LOGIN_TABLE = AUTH_COMMON_LOGIN + ".`USER_LOGIN`";
  public static final String LOGIN_SECURE_KEY_TABLE = AUTH_COMMON_LOGIN + ".`login_secure_key`";
}
