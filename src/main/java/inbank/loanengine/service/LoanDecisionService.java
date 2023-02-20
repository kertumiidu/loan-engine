package inbank.loanengine.service;

import inbank.loanengine.domain.ApplicantCreditInfo;
import inbank.loanengine.domain.LoanApplicationDetails;
import inbank.loanengine.domain.LoanDecisionResponse;
import inbank.loanengine.repository.CreditInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UP;

/**
 * This service class calculates possible loan offer
 * For calculating loan offer applicant credit information and loan application is required
 */

@Service
@AllArgsConstructor
public class LoanDecisionService {


  private CreditInfoRepository creditInfoRepository;
  private final BigDecimal MAX_LOAN_AMOUNT = new BigDecimal(10000);
  private final int MAX_LOAN_PERIOD_MONTHS = 60;
  private final int MIN_LOAN_PERIOD_MONTHS = 12;
  private final BigDecimal OPTIMAL_CREDIT_SCORE = new BigDecimal(1);

  /**
   * This method constructs loan decision
   * In case of debtor or missing credit info, loan application is declined and no further calculations are made
   *
   * @param loanApplicationDetails Loan applicant personal code, requested loan amount, requested loan period
   * @return Loan decision with possible loan terms according to credit score and initial loan application. In case of debtor, only decision is returned.
   */
  public LoanDecisionResponse getLoanDecision(LoanApplicationDetails loanApplicationDetails) {
    ApplicantCreditInfo applicantCreditInfo = creditInfoRepository.getCreditInfoByPersonalCode(loanApplicationDetails.getPersonalCode());

    if (applicantCreditInfo == null || applicantCreditInfo.getIsDebtor()){
      return LoanDecisionResponse.builder().loanGranted(false).loanApplicationDetails(loanApplicationDetails).build();
    }

    return calculateApplicableLoan(applicantCreditInfo, loanApplicationDetails);
  }

  /**
   * This method calculates possible loan terms according to applicant credit information and loan application
   * For applicable loan terms, credit score must be 1
   * In case credit score is not 1, this method calculates suitable loan amount or period
   *
   * @param loanApplicationDetails Loan applicant personal code, requested loan amount, requested loan period
   * @return Loan decision with possible loan terms according to credit score and initial loan application
   */
  private LoanDecisionResponse calculateApplicableLoan(ApplicantCreditInfo applicantCreditInfo, LoanApplicationDetails loanApplicationDetails) {
    LoanDecisionResponse loanDecisionResponse = new LoanDecisionResponse();
    BigDecimal loanAmount = loanApplicationDetails.getLoanAmount();
    int loanPeriod = loanApplicationDetails.getLoanPeriod();
    int creditModifier = applicantCreditInfo.getCreditModifier();

    BigDecimal creditScore = getCreditScore(applicantCreditInfo.getCreditModifier(), loanApplicationDetails.getLoanAmount(), loanApplicationDetails.getLoanPeriod());

    if (exceedsRequiredScore(creditScore)) {
      calculateMaxLoanAmount(loanPeriod, creditModifier, loanDecisionResponse);
    }

    if (lessThanRequiredScore(creditScore)) {
      if (exceedsMaximumPeriod(applicantCreditInfo, loanAmount, loanDecisionResponse)) {
        return loanDecisionResponse.setLoanGranted(false);
      }
    }
    return loanDecisionResponse.setLoanApplicationDetails(loanApplicationDetails);
  }

  private boolean exceedsMaximumPeriod(ApplicantCreditInfo applicantCreditInfo, BigDecimal loanAmount, LoanDecisionResponse loanDecisionResponse) {
    int suggestedLoanPeriod = getLoanPeriodSuggestion(loanAmount, applicantCreditInfo.getCreditModifier());
    if (suggestedLoanPeriod < MAX_LOAN_PERIOD_MONTHS && suggestedLoanPeriod > MIN_LOAN_PERIOD_MONTHS) {
      loanDecisionResponse.setLoanGranted(true).setOfferAmount(loanAmount).setOfferPeriod(suggestedLoanPeriod);
    } else {
      return true;
    }
    return false;
  }

  private void calculateMaxLoanAmount(int loanPeriod, int creditModifier, LoanDecisionResponse loanDecisionResponse) {
    BigDecimal maxOfferAmount = getMaxLoanAmountForRequestedPeriod(creditModifier, loanPeriod);
    if (exceedsMaximumLoanAmount(maxOfferAmount)) {
      loanDecisionResponse.setLoanGranted(true).setOfferAmount(MAX_LOAN_AMOUNT).setOfferPeriod(loanPeriod);
    } else {
      loanDecisionResponse.setLoanGranted(true).setOfferAmount(maxOfferAmount).setOfferPeriod(loanPeriod);
    }
  }

  private int getLoanPeriodSuggestion(BigDecimal loanAmount, int creditModifier) {
    return loanAmount.divide(BigDecimal.valueOf(creditModifier), UP).intValue();
  }

  private BigDecimal getMaxLoanAmountForRequestedPeriod(int creditModifier, int loanPeriod) {
    return BigDecimal.valueOf(creditModifier * loanPeriod);
  }

  private BigDecimal getCreditScore(int creditModifier, BigDecimal loanAmount, int loanPeriod) {
    return (BigDecimal.valueOf(creditModifier).divide(loanAmount, HALF_UP)).multiply(BigDecimal.valueOf(loanPeriod));
  }

  private boolean exceedsRequiredScore(BigDecimal creditScore) {
    return creditScore.compareTo(OPTIMAL_CREDIT_SCORE) >= 1;
  }
  private boolean lessThanRequiredScore(BigDecimal creditScore) {
    return creditScore.compareTo(OPTIMAL_CREDIT_SCORE) < 1;
  }

  private boolean exceedsMaximumLoanAmount(BigDecimal maxOfferAmount) {
    return maxOfferAmount.compareTo(MAX_LOAN_AMOUNT) >= 0;
  }
}
