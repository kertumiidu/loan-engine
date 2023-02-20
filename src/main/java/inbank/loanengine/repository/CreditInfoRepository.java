package inbank.loanengine.repository;

import inbank.loanengine.domain.ApplicantCreditInfo;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * This repository class uses H2 database for retrieving applicant credit info
 */
@Repository
@AllArgsConstructor
public class CreditInfoRepository {

  private JdbcTemplate jdbcTemplate;
  private DataSource dataSource;

  @PostConstruct
  private void postConstruct() {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public ApplicantCreditInfo getCreditInfoByPersonalCode(String loanApplicantPersonalCode) {
    BeanPropertyRowMapper<ApplicantCreditInfo> rowMapper = new BeanPropertyRowMapper<>(ApplicantCreditInfo.class);
    String sql = """
           SELECT *
           FROM applicants_credit_info
           WHERE personal_code = ?;
           """;

    try {
      return jdbcTemplate.queryForObject(sql, rowMapper, loanApplicantPersonalCode);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

}
