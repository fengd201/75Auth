package com.fengd201.auth.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Base64;
import javax.crypto.SecretKey;

import com.fengd201.auth.common.constant.UserManagerConstants;
import com.fengd201.auth.modal.entity.UserLoginInfo;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserLoginManagerTest {
  UserLoginManager userLoginManager = new UserLoginManager();
  private final static String LOGIN_NAME = "testLoginName5555";
  private String uuid = "";

  @Ignore
  @Test
  public void test1_testCreateNewUser() {
    try {
      boolean res = userLoginManager.createNewUser(LOGIN_NAME, "abcde12345", 3, 3, "abc@test.com",
          "firstName", "middleName", "lastName", "");
      assertTrue(res);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test2_testLogin() {
    try {
      UserLoginInfo res = userLoginManager.login(LOGIN_NAME, "abcde12345");
      assertTrue(res.getLoginCode() == UserManagerConstants.LOGIN_SUCCESSFUL);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test3_testChangeUserPassword() {
    try {
      boolean res = userLoginManager.changeUserPassword(LOGIN_NAME, "abcde12345", "abcde123456");
      assertTrue(res);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test4_testGetBasicUserInfo() {
    try {
      UserLoginInfo res = userLoginManager.getBasicUserInfo(LOGIN_NAME);
      assertNotNull(res);
      assertTrue(!res.getLoginName().isEmpty());
      assertTrue(!res.getFirstName().isEmpty());
      assertTrue(!res.getLastName().isEmpty());
      assertTrue(res.getUserType() != 0);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  // @Test
  // public void test5_testGetBasicPatientInfo() {
  // try {
  // UserLoginInfo res = userLoginManager.getBasicPatientInfo("demopatient1");
  // assertNotNull(res);
  // assertTrue(!res.getLoginName().isEmpty());
  // assertTrue(!res.getFirstName().isEmpty());
  // assertTrue(!res.getLastName().isEmpty());
  // }
  // catch (Exception e) {
  // e.printStackTrace();
  // fail("Unexpected Exception: " + e);
  // }
  // }

  @Ignore
  @Test
  public void test6_testGenerateUniversallyUniqueId() {
    try {
      String uuid = userLoginManager.generateUniversallyUniqueId(LOGIN_NAME);
      assertTrue(!uuid.isEmpty());
      this.uuid = uuid;
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test7_testGetBasicUserInfoByUUID() {
    try {
      test6_testGenerateUniversallyUniqueId();
      UserLoginInfo res = userLoginManager.getBasicUserInfoByUUID(uuid);
      assertNotNull(res);
      assertTrue(!res.getLoginName().isEmpty());
      assertTrue(res.getUserType() != 0);
      assertTrue(res.getUserId() != 0);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test8_testIsPwned() {
    try {
      int res = userLoginManager.isPwned("abcde12345");
      System.out
          .println("This password has previously appreared in a data breach, match count: " + res);
      assertTrue(res > 0);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test99_testUpdateSecretKey() {
    try {
      userLoginManager.updateSecretKey();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test9_testGetSecureKey() {
    try {
      SecretKey key = userLoginManager.getSecureKey();
      String keyStr = Base64.getEncoder().encodeToString(key.getEncoded());
      System.out.println(keyStr);
      assertNotNull(keyStr);
      assertTrue(keyStr.length() == 24);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test10_testGenerateUserToken() {
    try {
      UserLoginInfo userInfo = new UserLoginInfo();
      userInfo.setUserId(12345);
      String token = userLoginManager.generateUserToken(userInfo, 1);
      assertNotNull(token);
      assertTrue(!token.isEmpty());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test11_testDecodeUserToken() {
    try {
      UserLoginInfo userInfo = new UserLoginInfo();
      userInfo.setUserId(12345);
      String token = userLoginManager.generateUserToken(userInfo, 1);
      userInfo = userLoginManager.decodeUserToken(token);
      assertNotNull(userInfo);
      assertTrue(userInfo.getUserId() == 12345);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test12_testEncryptString() {
    try {
      String str = "abcde";
      String encryptedStr = userLoginManager.encryptString(str, 1);
      assertNotNull(encryptedStr);
      assertTrue(!encryptedStr.isEmpty());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }

  @Ignore
  @Test
  public void test13_testDecryptString() {
    try {
      String str = "abcde";
      String encryptedStr = userLoginManager.encryptString(str, 1);
      String res = userLoginManager.decryptString(encryptedStr);
      assertNotNull(res);
      assertTrue("abcde".equals(res));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected Exception: " + e);
    }
  }
}
