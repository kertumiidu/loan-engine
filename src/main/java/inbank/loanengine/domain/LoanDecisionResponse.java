package inbank.loanengine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * This class is model for loan application response
 * This class contains loan decision based on applicant credit information, applicant personal code, offered loan amount and loan period and initial application details
 * For constructing LoanApplicationDetails all params are not required in case loan application is declined because of debt
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LoanDecisionResponse {
  private boolean loanGranted;
  private BigDecimal offerAmount;
  private int offerPeriod;
  private LoanApplicationDetails loanApplicationDetails;
}
