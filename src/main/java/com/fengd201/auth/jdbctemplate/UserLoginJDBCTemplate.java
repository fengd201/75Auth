package com.fengd201.auth.jdbctemplate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import com.fengd201.auth.common.constant.SqlConstants;
import com.fengd201.auth.dao.UserLoginDAO;
import com.fengd201.auth.modal.entity.UserLoginInfo;
import com.fengd201.auth.modal.mapper.UserLoginMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

public class UserLoginJDBCTemplate implements UserLoginDAO {
  private DataSource dataSource;
  private JdbcTemplate jdbcTemplate;

  @Override
  public void setDataSource(DataSource ds) {
    this.dataSource = ds;
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public int create(String loginName, String password, int userType, int roleId, String email,
      String firstName, String middleName, String lastName, String accountName) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource)
        .withCatalogName(SqlConstants.AUTH_COMMON_LOGIN)
        .withProcedureName(SqlConstants.CREATE_NEW_USER_PROCEDURE);
    SqlParameterSource in = new MapSqlParameterSource().addValue("i_login_name", loginName)
        .addValue("i_user_type", userType).addValue("i_role_id", roleId)
        .addValue("i_login_password", password).addValue("i_email", email)
        .addValue("i_first_name", firstName).addValue("i_middle_name", middleName)
        .addValue("i_last_name", lastName).addValue("i_account_name", accountName);
    Map<String, Object> out = simpleJdbcCall.execute(in);
    return (int) out.get("result_code");
  }

  @Override
  public UserLoginInfo getUserLoginInfo(String loginName) {
    final String sql = "call " + SqlConstants.FETCH_PASS_AND_ACCESS_ROLE_PROCEDURE + "(?)";
    UserLoginInfo userLoginInfo = jdbcTemplate.queryForObject(sql, new Object[] { loginName },
        new UserLoginMapper());
    return userLoginInfo;
  }

  @Override
  public void updatePassword(String loginName, String newPassword) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource)
        .withCatalogName(SqlConstants.AUTH_COMMON_LOGIN)
        .withProcedureName(SqlConstants.RESET_USER_PASSWORD_PROCEDURE);
    SqlParameterSource in = new MapSqlParameterSource().addValue("i_login_name", loginName)
        .addValue("i_password", newPassword);
    simpleJdbcCall.execute(in);
  }

  @Override
  public List<String> getPrevUserPassword(String loginName) throws SQLException {
    List<String> passList = new ArrayList<>();
    Connection conn = dataSource.getConnection();
    final String sql = "{call " + SqlConstants.FETCH_PREVIOUS_PASSWORD_PROCEDURE + "(?, ?)};";
    CallableStatement cStmt = conn.prepareCall(sql);
    try {
      cStmt.setString(1, loginName);
      cStmt.registerOutParameter(2, Types.VARCHAR);
      boolean hasRes = cStmt.execute();
      String hashOldPass = cStmt.getString(2);
      passList.add(hashOldPass);
      if (hasRes) {
        ResultSet rs = cStmt.getResultSet();
        while (rs.next()) {
          passList.add(rs.getString("password"));
        }
        rs.close();
      }
      return passList;
    } finally {
      try {
        cStmt.close();
      } catch (Exception e) {
        /* ignored */ }
      try {
        conn.close();
      } catch (Exception e) {
        /* ignored */ }
    }
  }

  @Override
  public UserLoginInfo getBasicUserInfo(String loginName) {
    final String sql = "SELECT login_name, user_type, email, first_name, last_name FROM "
        + SqlConstants.USER_LOGIN_TABLE + "WHERE login_name = ?";
    UserLoginInfo userLoginInfo = jdbcTemplate.queryForObject(sql, new Object[] { loginName },
        new UserLoginMapper());
    return userLoginInfo;
  }

  @Override
  public UserLoginInfo getBasicUserInfoByUUID(String uuid) {
    final String sql = "SELECT login_name, user_id, user_type FROM " + SqlConstants.USER_LOGIN_TABLE
        + " WHERE uuid = ?";
    UserLoginInfo userLoginInfo = jdbcTemplate.queryForObject(sql, new Object[] { uuid },
        new UserLoginMapper());
    return userLoginInfo;
  }

  @Override
  public int updateUniversallyUniqueId(String loginName, String uuid) {
    final String sql = "UPDATE " + SqlConstants.USER_LOGIN_TABLE
        + " SET uuid = ? WHERE login_name = ?";
    return jdbcTemplate.update(sql, uuid, loginName);
  }

}
