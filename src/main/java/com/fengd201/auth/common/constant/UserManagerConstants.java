package com.fengd201.auth.common.constant;

public class UserManagerConstants {
  public static int LOGIN_INFO_EXIST = -2;
  public static int LOGIN_INFO_NOT_UPDATED = -1;
  public static int ERROR_CODE_REQUIRED_FIELDS_MISSING = 1;
  public static int LOGIN_FAILED = -1;
  public static int LOGIN_SUCCESSFUL = 1;
  public static int PASSWORD_RESET_SUCCESSFUL = 1;
  public static int SAME_AS_PREVIOUS_PASSWORD = -2;
  public static String ERROR_REQUIRED_FIELDS_MISSING = "Required fields are missing.";
  public static String ERROR_LOGIN_INFO_EXIST = "Login information already exists.";
  public static String ERROR_FAILED_TO_CREATE_USER = "Failed to create new user.";
  public static String ERROR_CONVERT_PASS_TO_HASH = "Error in converting password to hash.";
  public static String ERROR_CREATE_NEW_USER = "Error in creating new user.";
  public static String ERROR_LOGIN_FAILED = "Login failed.";
  public static String ERROR_USER_PASSWORD_WRONG = "Username or password is incorrect.";
  public static String ERROR_CANNOT_USE_PREVIOUS_PASSWORD = "You cannot use your previous password again, please try another one.";
  public static String ERROR_FAILED_TO_RESET_PASSWORD = "Error in reseting password.";
  public static String ERROR_FAILED_TO_GET_USER_INFO = "Error in fetching basic user information.";
  public static String ERROR_FAILED_TO_GENERATE_UUID = "Error in generating universally unique identifier(UUID).";
  public static String WARNING_PASSWORD_IS_PWNED = "This password has previously appreared in a data breach";
  public static String ERROR_FAILED_TO_GENERATE_KEY = "Error in generating secure key.";
  public static String ERROR_FAILED_TO_FETCH_SECURE_KEY = "Unable to fetch secure key from database.";
  public static String ERROR_SIGNATURE_DOES_NOT_MATCH = "The signature does not match the local key. It may because of the secure key has been updated recently, or a fake request from an unauthorized issuer.";
  public static String ERROR_FAILED_TO_DECODE_TOKEN = "Error: Unable to decode Jwt token.";
  public static String WARNING_TOKEN_EXPIRED = "Token is expired.";

  // login table columns
  public static String USER_ID = "user_id";
  public static String LOGIN_NAME = "login_name";
  public static String USER_TYPE = "user_type";
  public static String PASSWORD = "login_password";
  public static String EMAIL = "email";
  public static String FIRST_NAME = "first_name";
  public static String MIDDLE_NAME = "middle_name";
  public static String LAST_NAME = "last_name";
  public static String ACCOUNT_NAME = "account_name";
  public static String RESET_FLAG = "reset_flg";

  // access role table columns
  public static String ROLE_ID = "role_id";

  public static String PWNEDPASSWORDS_API_URL = "https://api.pwnedpasswords.com/range/";
  public static int PWN_API_PARAM_LENGTH = 5;

  public static String CACHE_SECURE_KEY = "security_key";
  public static String CLAIM_KEY_USER_INFO = "userInfo";
  public static String VA_TOKEN_ISSUER = "https://www.75000g.com";
}
