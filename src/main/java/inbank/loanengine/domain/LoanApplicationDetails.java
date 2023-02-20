package inbank.loanengine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * This class is model for loan application details
 * This class contains loan applicant personal code, requested loan amount and requested loan period
 * For constructing LoanApplicationDetails all params are required
 */

@Data
@Builder
@AllArgsConstructor
public class LoanApplicationDetails {
  private String personalCode;
  private Integer loanPeriod;
  private BigDecimal loanAmount;
}
