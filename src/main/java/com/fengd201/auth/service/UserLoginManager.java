package com.fengd201.auth.service;

import com.fengd201.auth.common.cache.CacheManager;
import com.fengd201.auth.common.cache.CachedObject;
import com.fengd201.auth.common.constant.HttpMethod;
import com.fengd201.auth.common.constant.UserManagerConstants;
import com.fengd201.auth.exception.UserManageRecoverableException;
import com.fengd201.auth.exception.UserManageSystemException;
import com.fengd201.auth.jdbctemplate.SecretKeyTemplate;
import com.fengd201.auth.jdbctemplate.UserLoginJDBCTemplate;
import com.fengd201.auth.modal.entity.UserLoginInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;

public class UserLoginManager {

  private String errorDesc;
  private final static Logger logger = LogManager.getLogger(UserLoginManager.class);
  private ApplicationContext context;
  private UserLoginJDBCTemplate userLoginJDBCTemplate;

  public UserLoginManager() {
    this.errorDesc = "";
    this.context = new ClassPathXmlApplicationContext("Beans.xml");
    this.userLoginJDBCTemplate = (UserLoginJDBCTemplate) context.getBean("userLoginJDBCTemplate");
  }

  /**
   * Method to create new web user.
   * 
   * update errorDesc for any errors occurred while creating new users. update password history for
   * the user.
   * 
   * @param loginName
   *          required
   * @param password
   *          required
   * @param userGroup
   *          required
   * @param accessRole
   *          required
   * @param email
   *          optional
   * @param firstName
   *          required
   * @param middleName
   *          optional
   * @param lastName
   *          required
   * @param accountName
   *          optional
   * @return boolean
   * @throws UserManageException
   */
  // public boolean createNewUser(String loginName, String password,
  // SessionAttributesForAudit.UserGroup userGroup,
  // SessionAttributesForAudit.AccessRole accessRole, String email, String firstName,
  // String middleName, String lastName, String accountName) throws UserManageException {
  // if (userGroup == null || accessRole == null) {
  // logger.error(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
  // setErrorDesc(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
  // return false;
  // }
  // return createNewUser(loginName, password, userGroup.getId(), accessRole.getId(), email,
  // firstName, middleName, lastName, accountName);
  // }

  /**
   * Method to create new web users.
   * 
   * update errorDesc for any errors occurred while creating new users. update password history for
   * the user.
   * 
   * @param loginName
   *          required
   * @param password
   *          required
   * @param userType
   *          required
   * @param roleId
   *          required
   * @param email
   *          optional
   * @param firstName
   *          required
   * @param middleName
   *          optional
   * @param lastName
   *          required
   * @param accountName
   *          optional
   * @return boolean
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   */
  public boolean createNewUser(String loginName, String password, int userType, int roleId,
      String email, String firstName, String middleName, String lastName, String accountName)
      throws UserManageSystemException, UserManageRecoverableException {
    if (isNullOrEmpty(loginName) || isNullOrEmpty(password) || isNullOrEmpty(firstName)
        || isNullOrEmpty(lastName) || userType == 0 || roleId == 0) {
      logger.error(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      setErrorDesc(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      throw new UserManageSystemException(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
    }
    buildOptionalInputs(email, middleName, accountName);
    try {
      String passHash = PasswordManager.createHash(password);
      int resCode = userLoginJDBCTemplate.create(loginName, passHash, userType, roleId, email,
          firstName, middleName, lastName, accountName);
      if (resCode == UserManagerConstants.LOGIN_INFO_EXIST) {
        setErrorDesc(UserManagerConstants.ERROR_LOGIN_INFO_EXIST);
        logger.error(UserManagerConstants.ERROR_LOGIN_INFO_EXIST);
        return false;
      } else if (resCode == UserManagerConstants.LOGIN_INFO_NOT_UPDATED) {
        setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_CREATE_USER);
        logger.error(UserManagerConstants.ERROR_FAILED_TO_CREATE_USER);
        return false;
      }
      return true;
    } catch (InvalidKeySpecException ikse) {
      logger.error(UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, ikse);
      throw new UserManageSystemException(loginName,
          UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, ikse);
    } catch (NoSuchAlgorithmException nsae) {
      logger.error(UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, nsae);
      throw new UserManageSystemException(loginName,
          UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, nsae);
    } catch (RecoverableDataAccessException rdae) {
      logger.error(UserManagerConstants.ERROR_CREATE_NEW_USER, rdae);
      throw new UserManageRecoverableException(loginName,
          UserManagerConstants.ERROR_CREATE_NEW_USER, rdae);
    } catch (DataAccessException dae) {
      logger.error(UserManagerConstants.ERROR_CREATE_NEW_USER, dae);
      throw new UserManageSystemException(loginName, UserManagerConstants.ERROR_CREATE_NEW_USER,
          dae);
    }
  }

  /**
   * Method to validate user login password and fetch user details.
   * 
   * update errorDesc for any errors occurred while logging in.
   * 
   * @param loginName
   *          required
   * @param password
   *          required
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   * 
   */
  public UserLoginInfo login(String loginName, String password)
      throws UserManageSystemException, UserManageRecoverableException {
    return loginCommon(loginName, password);
  }

  private UserLoginInfo loginCommon(String loginName, String password)
      throws UserManageSystemException, UserManageRecoverableException {
    UserLoginInfo userLoginInfo = new UserLoginInfo();
    try {
      String userPassword = "";
      userLoginInfo = userLoginJDBCTemplate.getUserLoginInfo(loginName);
      userLoginInfo.setLoginName(loginName);
      userPassword = userLoginInfo.getPassword();
      // validate user password
      if (PasswordManager.validatePassword(password, userPassword)) {
        userLoginInfo.setLoginCode(UserManagerConstants.LOGIN_SUCCESSFUL);
      } else {
        logger.info("Login by user " + loginName + " failed!");
        setErrorDesc("Login by user " + loginName + " failed!");
        userLoginInfo = new UserLoginInfo();
        userLoginInfo.setLoginCode(UserManagerConstants.LOGIN_FAILED);
      }
    } catch (InvalidKeySpecException ikse) {
      userLoginInfo.setLoginCode(UserManagerConstants.LOGIN_FAILED);
      logger.error(UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, ikse);
      throw new UserManageSystemException(loginName,
          UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, ikse);
    } catch (NoSuchAlgorithmException nsae) {
      userLoginInfo.setLoginCode(UserManagerConstants.LOGIN_FAILED);
      logger.error(UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, nsae);
      throw new UserManageSystemException(loginName,
          UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, nsae);
    } catch (RecoverableDataAccessException rdae) {
      userLoginInfo.setLoginCode(UserManagerConstants.LOGIN_FAILED);
      logger.error(UserManagerConstants.ERROR_LOGIN_FAILED, rdae);
      throw new UserManageRecoverableException(loginName, UserManagerConstants.ERROR_LOGIN_FAILED,
          rdae);
    } catch (DataAccessException dae) {
      userLoginInfo.setLoginCode(UserManagerConstants.LOGIN_FAILED);
      logger.error(UserManagerConstants.ERROR_LOGIN_FAILED, dae);
      throw new UserManageSystemException(loginName, UserManagerConstants.ERROR_LOGIN_FAILED, dae);
    }
    return userLoginInfo;
  }

  /**
   * Method to change user login password.
   * 
   * update errorDesc for any errors occurred while reseting password. update password history for
   * the user. users are not allowed to change to previous passwords which have been used in last 3
   * months.
   * 
   * @param loginName
   *          required
   * @param oldPassword
   *          required
   * @param newPassword
   *          required
   * @throws UserManageSystemException
   * 
   */
  public boolean changeUserPassword(String loginName, String oldPassword, String newPassword)
      throws UserManageSystemException {
    if (isNullOrEmpty(loginName) || isNullOrEmpty(oldPassword) || isNullOrEmpty(newPassword)) {
      logger.error(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      setErrorDesc(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      throw new UserManageSystemException(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
    }
    try {
      List<String> passList = userLoginJDBCTemplate.getPrevUserPassword(loginName);
      // validate current password
      if (passList.isEmpty() || !PasswordManager.validatePassword(oldPassword, passList.get(0))) {
        setErrorDesc(UserManagerConstants.ERROR_USER_PASSWORD_WRONG);
        logger.error(UserManagerConstants.ERROR_USER_PASSWORD_WRONG);
        return false;
      }
      // users are not allowed to change to previous passwords which have been used in last 3 months
      for (int i = 1; i < passList.size(); i++) {
        if (PasswordManager.validatePassword(newPassword, passList.get(i))) {
          setErrorDesc(UserManagerConstants.ERROR_CANNOT_USE_PREVIOUS_PASSWORD);
          logger.error(UserManagerConstants.ERROR_CANNOT_USE_PREVIOUS_PASSWORD);
          return false;
        }
      }
      // update password and password history for the user
      String hashNewPass = PasswordManager.createHash(newPassword);
      userLoginJDBCTemplate.updatePassword(loginName, hashNewPass);
      return true;
    } catch (InvalidKeySpecException ikse) {
      logger.error(UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, ikse);
      throw new UserManageSystemException(loginName,
          UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, ikse);
    } catch (NoSuchAlgorithmException nsae) {
      logger.error(UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, nsae);
      throw new UserManageSystemException(loginName,
          UserManagerConstants.ERROR_CONVERT_PASS_TO_HASH, nsae);
    } catch (SQLException sqle) {
      logger.error(UserManagerConstants.ERROR_FAILED_TO_RESET_PASSWORD, sqle);
      throw new UserManageSystemException(loginName,
          UserManagerConstants.ERROR_FAILED_TO_RESET_PASSWORD, sqle);
    }
  }

  /**
   * Method to get first name, last name, user type, and email address of given user name
   * 
   * @param loginName
   *          required
   * @return UserLoginInfo
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   * 
   */
  public UserLoginInfo getBasicUserInfo(String loginName)
      throws UserManageSystemException, UserManageRecoverableException {
    if (isNullOrEmpty(loginName)) {
      logger.error(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      setErrorDesc(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      throw new UserManageSystemException(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
    }
    try {
      return userLoginJDBCTemplate.getBasicUserInfo(loginName);
    } catch (RecoverableDataAccessException rdae) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO, rdae);
      throw new UserManageRecoverableException(loginName,
          UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO, rdae);
    } catch (DataAccessException dae) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO, dae);
      throw new UserManageSystemException(loginName,
          UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO, dae);
    }
  }

  /**
   * Method to get login name, user id, and user type by given UUID
   * 
   * @param uuid
   *          required
   * @return UserLoginInfo
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   * 
   */
  public UserLoginInfo getBasicUserInfoByUUID(String uuid)
      throws UserManageSystemException, UserManageRecoverableException {
    if (isNullOrEmpty(uuid)) {
      logger.error(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      setErrorDesc(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      throw new UserManageSystemException(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
    }
    try {
      return userLoginJDBCTemplate.getBasicUserInfoByUUID(uuid);
    } catch (RecoverableDataAccessException rdae) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO, rdae);
      throw new UserManageRecoverableException(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO,
          rdae);
    } catch (DataAccessException dae) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO, dae);
      throw new UserManageSystemException(UserManagerConstants.ERROR_FAILED_TO_GET_USER_INFO, dae);
    }
  }

  /**
   * Method to generate the universally unique identifier for users.
   * 
   * uuid will be updated each time this method is called. call this method before sending reset
   * password link.
   * 
   * @param loginName
   *          required
   * @return String
   * @throws UserManageSystemException
   * 
   */
  public String generateUniversallyUniqueId(String loginName) throws UserManageSystemException {
    String uuid = "";
    if (isNullOrEmpty(loginName)) {
      logger.error(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      setErrorDesc(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      throw new UserManageSystemException(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
    }
    uuid = UUID.randomUUID().toString();
    int res = userLoginJDBCTemplate.updateUniversallyUniqueId(loginName, uuid);
    if (res == -1) {
      uuid = "";
      logger.error(UserManagerConstants.ERROR_FAILED_TO_GENERATE_UUID);
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_GENERATE_UUID);
    }
    return uuid;
  }

  /**
   * Method to check if a password is pwned.
   * 
   * ws rate limited to one request every 1,500ms per IP address
   * 
   * Api doc: https://haveibeenpwned.com/API/v2#SearchingPwnedPasswordsByRange
   * 
   * returns 0: given password is a good password. returns positive number: given password is pwned,
   * return value is the number of matched passwords found in pwned passwords db.
   * 
   * @param password
   *          required
   * @return int
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   * 
   */
  public int isPwned(String password)
      throws UserManageSystemException, UserManageRecoverableException {
    if (password == null || password.isEmpty()) {
      logger.error(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      setErrorDesc(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
      throw new UserManageSystemException(UserManagerConstants.ERROR_REQUIRED_FIELDS_MISSING);
    }
    Map<String, Integer> passMap = new HashMap<>();
    String sha1 = DigestUtils.shaHex(password).toUpperCase();
    try {
      // pass first 5 characters of a SHA-1 password hash
      String url = UserManagerConstants.PWNEDPASSWORDS_API_URL
          + sha1.substring(0, UserManagerConstants.PWN_API_PARAM_LENGTH);
      String response = WebRequestUtil.sendRestRequest(url, HttpMethod.GET, "VA-WEB");
      String[] pairs = response.split("\r");
      for (String pair : pairs) {
        String[] pass = pair.split(":");
        passMap.put(pass[0], Integer.parseInt(pass[1]));
      }
    } catch (Exception e) {
      throw new UserManageRecoverableException(e);
    }
    if (passMap.containsKey(sha1.substring(UserManagerConstants.PWN_API_PARAM_LENGTH))) {
      setErrorDesc(UserManagerConstants.WARNING_PASSWORD_IS_PWNED);
      return passMap.get(sha1.substring(UserManagerConstants.PWN_API_PARAM_LENGTH));
    } else {
      return 0;
    }
  }

  /**
   * method to generate/update secure key to encode or decode users' token
   * 
   * DO NOT call this method unless you think the key is leaked
   * 
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   */
  public void updateSecretKey() throws UserManageSystemException, UserManageRecoverableException {
    try {
      SecretKeyTemplate secretKeyTemplate = (SecretKeyTemplate) context
          .getBean("secretKeyTemplate");
      KeyGenerator keyGen = KeyGenerator.getInstance("AES");
      keyGen.init(128);
      SecretKey key = keyGen.generateKey();
      String keyStr = Base64.getEncoder().encodeToString(key.getEncoded());
      System.out.println(keyStr.length());
      Timestamp ts = new Timestamp(System.currentTimeMillis());
      secretKeyTemplate.create(keyStr, ts);
      // remove old key from cache
      CacheManager.removeCache(UserManagerConstants.CACHE_SECURE_KEY);
    } catch (NoSuchAlgorithmException nsae) {
      throw new UserManageSystemException(nsae);
    } catch (RecoverableDataAccessException rdae) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_GENERATE_KEY);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_GENERATE_KEY);
      throw new UserManageRecoverableException(rdae);
    } catch (DataAccessException dae) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_GENERATE_KEY);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_GENERATE_KEY);
      throw new UserManageSystemException(dae);
    }
  }

  /**
   * method to decode the JWT tokens with secret key.
   * 
   * @param token
   * @return UserLoginInfo
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   */
  public UserLoginInfo decodeUserToken(String token)
      throws UserManageSystemException, UserManageRecoverableException {
    UserLoginInfo userInfo = new UserLoginInfo();
    try {
      SecretKey key = getSecureKey();
      Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
      userInfo = convertClaimToUserInfo(claims);
    } catch (UserManageSystemException umse) {
      throw umse;
    } catch (SignatureException se) {
      setErrorDesc(UserManagerConstants.ERROR_SIGNATURE_DOES_NOT_MATCH);
      logger.error(UserManagerConstants.ERROR_SIGNATURE_DOES_NOT_MATCH);
      throw new UserManageRecoverableException(se);
    } catch (ExpiredJwtException eje) {
      setErrorDesc(UserManagerConstants.WARNING_TOKEN_EXPIRED);
      logger.error(UserManagerConstants.WARNING_TOKEN_EXPIRED);
      throw new UserManageRecoverableException(eje);
    } catch (Exception e) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_DECODE_TOKEN);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_DECODE_TOKEN);
      throw new UserManageSystemException(e);
    }
    return userInfo;
  }

  /**
   * 
   * method to generate secure token for user information with secret key
   * 
   * 
   * @param userInfo
   * @param expireInDays
   * @return String
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   */
  public String generateUserToken(UserLoginInfo userInfo, int expireInDays)
      throws UserManageSystemException, UserManageRecoverableException {
    SecretKey key = getSecureKey();
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DAY_OF_YEAR, expireInDays);
    Date expireDate = cal.getTime();
    return Jwts.builder().setIssuer(UserManagerConstants.VA_TOKEN_ISSUER)
        .claim("userInfo", userInfo).setExpiration(expireDate)
        .signWith(SignatureAlgorithm.HS512, key).compact();
  }

  /**
   * 
   * method to encrypt a string and returns an encrypted token
   * 
   * 
   * @param str
   * @param expireInDays
   * @return String
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   */
  public String encryptString(String str, int expireInDays)
      throws UserManageSystemException, UserManageRecoverableException {
    SecretKey key = getSecureKey();
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DAY_OF_YEAR, expireInDays);
    Date expireDate = cal.getTime();
    return Jwts.builder().setIssuer(UserManagerConstants.VA_TOKEN_ISSUER).setId(str)
        .setExpiration(expireDate).signWith(SignatureAlgorithm.HS512, key).compact();
  }

  /**
   * 
   * method to decrypt a token and returns a String
   * 
   * @param encryptedStr
   * @return String
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   */
  public String decryptString(String encryptedStr)
      throws UserManageSystemException, UserManageRecoverableException {
    String str = "";
    try {
      SecretKey key = getSecureKey();
      Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(encryptedStr).getBody();
      str = claims.getId();
    } catch (UserManageSystemException umse) {
      throw umse;
    } catch (SignatureException se) {
      setErrorDesc(UserManagerConstants.ERROR_SIGNATURE_DOES_NOT_MATCH);
      logger.error(UserManagerConstants.ERROR_SIGNATURE_DOES_NOT_MATCH);
      throw new UserManageRecoverableException(se);
    } catch (ExpiredJwtException eje) {
      setErrorDesc(UserManagerConstants.WARNING_TOKEN_EXPIRED);
      logger.error(UserManagerConstants.WARNING_TOKEN_EXPIRED);
      throw new UserManageRecoverableException(eje);
    } catch (Exception e) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_DECODE_TOKEN);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_DECODE_TOKEN);
      throw new UserManageSystemException(e);
    }
    return str;
  }

  /**
   * method to get secure key to encode/decode login token.
   * 
   * will try to get key from cache first. If cache not found, fetch from DB and update cache.
   * 
   * @return SecretKey
   * @throws UserManageSystemException
   * @throws UserManageRecoverableException
   */
  public SecretKey getSecureKey() throws UserManageSystemException, UserManageRecoverableException {
    CachedObject keyObj = (CachedObject) CacheManager
        .getCache(UserManagerConstants.CACHE_SECURE_KEY);
    if (keyObj == null) {
      logger.info("Cannot find secure key in CACHE.");
      SecretKey key = fetchSecureKeyFromDB();
      CacheManager.putCache(new CachedObject(key, UserManagerConstants.CACHE_SECURE_KEY, 600));
      return key;
    } else {
      logger.info("Found secure key in CACHE.");
      return (SecretKey) keyObj.value;
    }
  }

  private SecretKey fetchSecureKeyFromDB()
      throws UserManageSystemException, UserManageRecoverableException {
    String aesStr = "";
    try {
      SecretKeyTemplate secretKeyTemplate = (SecretKeyTemplate) context
          .getBean("secretKeyTemplate");
      aesStr = secretKeyTemplate.getSecretKey();
    } catch (RecoverableDataAccessException rdae) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_FETCH_SECURE_KEY);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_FETCH_SECURE_KEY);
      throw new UserManageRecoverableException(rdae);
    } catch (DataAccessException dae) {
      setErrorDesc(UserManagerConstants.ERROR_FAILED_TO_FETCH_SECURE_KEY);
      logger.error(UserManagerConstants.ERROR_FAILED_TO_FETCH_SECURE_KEY);
      throw new UserManageSystemException(dae);
    }
    byte[] decodedKey = Base64.getDecoder().decode(aesStr);
    SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    return key;
  }

  private UserLoginInfo convertClaimToUserInfo(Claims claims) {
    UserLoginInfo userInfo = new UserLoginInfo();
    Map<String,
        Object> map = (Map<String, Object>) claims.get(UserManagerConstants.CLAIM_KEY_USER_INFO);
    userInfo.setLoginCode(map.get("loginCode") == null ? 0 : (int) map.get("loginCode"));
    userInfo.setUserId(map.get("userId") == null ? 0 : (int) map.get("userId"));
    userInfo.setUserType(map.get("userType") == null ? 0 : (int) map.get("userType"));
    userInfo.setFirstName(map.get("firstName") == null ? "" : (String) map.get("firstName"));
    userInfo.setMiddleName(map.get("middleName") == null ? "" : (String) map.get("middleName"));
    userInfo.setLastName(map.get("lastName") == null ? "" : (String) map.get("lastName"));
    userInfo.setAccountName(map.get("accountName") == null ? "" : (String) map.get("accountName"));
    userInfo.setEmail(map.get("email") == null ? "" : (String) map.get("email"));
    userInfo.setResetFlg(map.get("resetFlg") == null ? false : (boolean) map.get("resetFlg"));
    if (null != map.get("accessRolesList")) {
      userInfo.setAccessRolesList((List<Integer>) map.get("accessRolesList"));
    }
    return userInfo;
  }

  private boolean isNullOrEmpty(String str) {
    if (str == null || str.isEmpty())
      return true;
    return false;
  }

  private void buildOptionalInputs(String email, String middleName, String accountName) {
    if (email == null)
      email = "";
    if (middleName == null)
      middleName = "";
    if (accountName == null)
      accountName = "";
  }

  /**
   * @return the errorDesc
   */
  public String getErrorDesc() {
    return errorDesc;
  }

  /**
   * @param errorDesc
   *          the errorDesc to set
   */
  private void setErrorDesc(String errorDesc) {
    this.errorDesc = errorDesc;
  }

}
