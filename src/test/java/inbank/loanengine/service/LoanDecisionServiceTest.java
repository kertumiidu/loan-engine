package inbank.loanengine.service;

import inbank.loanengine.domain.ApplicantCreditInfo;
import inbank.loanengine.domain.LoanApplicationDetails;
import inbank.loanengine.domain.LoanDecisionResponse;
import inbank.loanengine.repository.CreditInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static java.math.RoundingMode.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanDecisionServiceTest {
  @InjectMocks
  private LoanDecisionService loanDecisionService;
  @Mock
  private CreditInfoRepository creditInfoRepository;

  @Test
  void getLoanDecisionWithMaximumAmountOffer() {
    LoanApplicationDetails loanApplicationDetails = LoanApplicationDetails.builder().personalCode("1234").loanAmount(BigDecimal.valueOf(2000)).loanPeriod(12).build();
    ApplicantCreditInfo applicantCreditInfo = ApplicantCreditInfo.builder().personalCode("1234").creditModifier(1000).isDebtor(false).build();

    when(creditInfoRepository.getCreditInfoByPersonalCode(loanApplicationDetails.getPersonalCode())).thenReturn(applicantCreditInfo);

    LoanDecisionResponse loanDecisionResponse = loanDecisionService.getLoanDecision(loanApplicationDetails);
    assertTrue(loanDecisionResponse.isLoanGranted());
    assertEquals(BigDecimal.valueOf(10000), loanDecisionResponse.getOfferAmount(), "Maximum loan offer can be 10000");
    assertEquals(12, loanDecisionResponse.getOfferPeriod(), "Should offer requested period");
    assertEquals(loanApplicationDetails, loanDecisionResponse.getLoanApplicationDetails(), "Loan application details should match");
  }

  @Test
  void getLoanDecisionWithSuggestedPeriodOffer() {
    LoanApplicationDetails loanApplicationDetails = LoanApplicationDetails.builder().personalCode("4567").loanAmount(BigDecimal.valueOf(4000)).loanPeriod(12).build();
    ApplicantCreditInfo applicantCreditInfo = ApplicantCreditInfo.builder().personalCode("4567").creditModifier(100).isDebtor(false).build();
    int suggestedLoanPeriod = loanApplicationDetails.getLoanAmount().divide(BigDecimal.valueOf(100) , UP).intValue();

    when(creditInfoRepository.getCreditInfoByPersonalCode(loanApplicationDetails.getPersonalCode())).thenReturn(applicantCreditInfo);

    LoanDecisionResponse loanDecisionResponse = loanDecisionService.getLoanDecision(loanApplicationDetails);
    assertTrue(loanDecisionResponse.isLoanGranted());
    assertEquals(BigDecimal.valueOf(4000), loanDecisionResponse.getOfferAmount(), "Should offer requested amount");
    assertEquals(suggestedLoanPeriod, loanDecisionResponse.getOfferPeriod(), "Should suggest modified period");
    assertEquals(loanApplicationDetails, loanDecisionResponse.getLoanApplicationDetails(), "Loan application details should match");
  }

  @Test
  void getLoanDecisionForPersonWithDebt() {
    LoanApplicationDetails loanApplicationDetails = LoanApplicationDetails.builder().personalCode("8910").loanAmount(BigDecimal.valueOf(4000)).loanPeriod(12).build();
    ApplicantCreditInfo applicantCreditInfo = ApplicantCreditInfo.builder().personalCode("8910").creditModifier(null).isDebtor(true).build();

    when(creditInfoRepository.getCreditInfoByPersonalCode(loanApplicationDetails.getPersonalCode())).thenReturn(applicantCreditInfo);

    LoanDecisionResponse loanDecisionResponse = loanDecisionService.getLoanDecision(loanApplicationDetails);

    assertFalse(loanDecisionResponse.isLoanGranted());
    assertNull(loanDecisionResponse.getOfferAmount(), "Can't offer loan if peron has debt");
    assertEquals(0, loanDecisionResponse.getOfferPeriod(), "Can't offer loan if peron has debt");
    assertEquals(loanApplicationDetails, loanDecisionResponse.getLoanApplicationDetails(), "Loan application details should match");
  }

  @Test
  void getLoanDecisionForPersonWithNoCreditInfo() {
    LoanApplicationDetails loanApplicationDetails = LoanApplicationDetails.builder().personalCode("1112").loanAmount(BigDecimal.valueOf(2000)).loanPeriod(12).build();
    when(creditInfoRepository.getCreditInfoByPersonalCode(loanApplicationDetails.getPersonalCode())).thenReturn(null);

    LoanDecisionResponse loanDecisionResponse = loanDecisionService.getLoanDecision(loanApplicationDetails);
    assertFalse(loanDecisionResponse.isLoanGranted());
    assertNull(loanDecisionResponse.getOfferAmount(), "");
    assertEquals(0, loanDecisionResponse.getOfferPeriod(), "");
    assertEquals(loanApplicationDetails, loanDecisionResponse.getLoanApplicationDetails(), "");
  }

}
