package com.fengd201.auth.modal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SecretKeyMapper implements RowMapper<String> {

  @Override
  public String mapRow(ResultSet rs, int rowNum) throws SQLException {
    String keyStr = "";
    if (rs.first())
      keyStr = rs.getString("aes_key");
    return keyStr;
  }

}
