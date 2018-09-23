package com.fengd201.auth.modal.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fengd201.auth.common.constant.UserManagerConstants;
import com.fengd201.auth.modal.entity.UserLoginInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

public class UserLoginMapper implements RowMapper<UserLoginInfo> {
  private Logger logger = LogManager.getLogger(UserLoginMapper.class);

  @Override
  public UserLoginInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
    UserLoginInfo userLoginInfo = new UserLoginInfo();
    List<Integer> accessRolesList = new ArrayList<>();
    Set<String> colSet = getReturnedColumns(rs);
    do {
      if (colSet.contains(UserManagerConstants.ROLE_ID))
        accessRolesList.add(rs.getInt(UserManagerConstants.ROLE_ID));
      if (colSet.contains(UserManagerConstants.USER_ID))
        userLoginInfo.setUserId(rs.getInt(UserManagerConstants.USER_ID));
      if (colSet.contains(UserManagerConstants.LOGIN_NAME))
        userLoginInfo.setLoginName(rs.getString(UserManagerConstants.LOGIN_NAME));
      if (colSet.contains(UserManagerConstants.USER_TYPE))
        userLoginInfo.setUserType(rs.getInt(UserManagerConstants.USER_TYPE));
      if (colSet.contains(UserManagerConstants.FIRST_NAME))
        userLoginInfo.setFirstName(rs.getString(UserManagerConstants.FIRST_NAME));
      if (colSet.contains(UserManagerConstants.MIDDLE_NAME))
        userLoginInfo.setMiddleName(rs.getString(UserManagerConstants.MIDDLE_NAME));
      if (colSet.contains(UserManagerConstants.LAST_NAME))
        userLoginInfo.setLastName(rs.getString(UserManagerConstants.LAST_NAME));
      if (colSet.contains(UserManagerConstants.ACCOUNT_NAME))
        userLoginInfo.setAccountName(rs.getString(UserManagerConstants.ACCOUNT_NAME));
      if (colSet.contains(UserManagerConstants.EMAIL))
        userLoginInfo.setEmail(rs.getString(UserManagerConstants.EMAIL));
      if (colSet.contains(UserManagerConstants.RESET_FLAG))
        userLoginInfo.setResetFlg(rs.getInt(UserManagerConstants.RESET_FLAG) == 1 ? true : false);
      if (colSet.contains(UserManagerConstants.PASSWORD))
        userLoginInfo.setPassword(rs.getString(UserManagerConstants.PASSWORD));
    } while (rs.next());
    userLoginInfo.setAccessRolesList(accessRolesList);
    return userLoginInfo;
  }

  private Set<String> getReturnedColumns(ResultSet rs) {
    Set<String> colSet = new HashSet<>();
    try {
      ResultSetMetaData rsMetaData = rs.getMetaData();
      int numberOfColumns = rsMetaData.getColumnCount();
      for (int i = 1; i <= numberOfColumns; i++) {
        colSet.add(rsMetaData.getColumnName(i).toLowerCase());
      }
    } catch (SQLException sqle) {
      logger.error("Failed to get returned columns from result set.", sqle);
    }
    return colSet;
  }
}
