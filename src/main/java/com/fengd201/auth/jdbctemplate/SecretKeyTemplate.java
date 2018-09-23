package com.fengd201.auth.jdbctemplate;


import java.sql.Timestamp;
import javax.sql.DataSource;

import com.fengd201.auth.common.constant.SqlConstants;
import com.fengd201.auth.dao.SecretKeyDAO;
import com.fengd201.auth.modal.mapper.SecretKeyMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class SecretKeyTemplate implements SecretKeyDAO {
  private DataSource dataSource;
  private JdbcTemplate jdbcTemplate;

  @Override
  public void setDataSource(DataSource ds) {
    this.dataSource = ds;
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public void create(String keyStr, Timestamp ts) {
    final String sql = "INSERT INTO " + SqlConstants.LOGIN_SECURE_KEY_TABLE + " VALUES (?, ?)";
    jdbcTemplate.update(sql, keyStr, ts);
  }

  @Override
  public String getSecretKey() {
    final String sql = "SELECT aes_key FROM " + SqlConstants.LOGIN_SECURE_KEY_TABLE
        + " WHERE created_at=(SELECT MAX(created_at) FROM " + SqlConstants.LOGIN_SECURE_KEY_TABLE
        + ")";
    String secretKey = jdbcTemplate.queryForObject(sql, new SecretKeyMapper());
    return secretKey;
  }

}
