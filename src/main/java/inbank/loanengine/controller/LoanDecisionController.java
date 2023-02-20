package inbank.loanengine.controller;

import inbank.loanengine.domain.LoanApplicationDetails;
import inbank.loanengine.domain.LoanDecisionResponse;
import inbank.loanengine.service.LoanDecisionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan-decision")
@AllArgsConstructor
public class LoanDecisionController {

  private LoanDecisionService loanDecisionService;

  /**
   * This method is for getting loan decision for application
   *
   * @param loanApplicationDetails Loan applicant personal code, requested loan amount, requested loan period
   * @return Response for initial loan application that contains decision and possible loan terms
   */
  @CrossOrigin
  @PostMapping("/application")
  public LoanDecisionResponse getLoanDecision(@RequestBody LoanApplicationDetails loanApplicationDetails) {
    return loanDecisionService.getLoanDecision(loanApplicationDetails);
  }
}
