package inbank.loanengine.controller;

import inbank.loanengine.domain.LoanApplicationDetails;
import inbank.loanengine.domain.LoanDecisionResponse;
import inbank.loanengine.service.LoanDecisionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(LoanDecisionController.class)
class LoanDecisionControllerTest {
  @MockBean
  LoanDecisionService loanDecisionService;

  @Autowired
  MockMvc mockMvc;

  @Test
  public void getLoanDecision() throws Exception {
    LoanApplicationDetails loanApplicationDetails = LoanApplicationDetails.builder()
      .personalCode("1234")
      .loanAmount(BigDecimal.valueOf(2000))
      .loanPeriod(12)
      .build();
    LoanDecisionResponse loanDecisionResponse = LoanDecisionResponse.builder()
      .loanGranted(true)
      .offerAmount(BigDecimal.valueOf(2000))
      .offerPeriod(12)
      .loanApplicationDetails(loanApplicationDetails)
      .build();

    when(loanDecisionService.getLoanDecision(loanApplicationDetails)).thenReturn(loanDecisionResponse);

    mockMvc.perform(post("/loan-decision/application")
        .content("{\"loanApplicantPersonalCode\": \"1234\", \"loanPeriod\": \"12\", \"loanAmount\": \"3000\"}")
        .contentType(APPLICATION_JSON))
      .andExpect(status().isOk());
  }

  @Test
  public void getLoanDecisionWithNoRequestBody() throws Exception {
    LoanApplicationDetails loanApplicationDetails = LoanApplicationDetails.builder()
      .personalCode("1234")
      .loanAmount(BigDecimal.valueOf(2000))
      .loanPeriod(12)
      .build();
    LoanDecisionResponse loanDecisionResponse = LoanDecisionResponse.builder()
      .loanGranted(true)
      .offerAmount(BigDecimal.valueOf(2000))
      .offerPeriod(12)
      .loanApplicationDetails(loanApplicationDetails)
      .build();

    when(loanDecisionService.getLoanDecision(loanApplicationDetails)).thenReturn(loanDecisionResponse);

    mockMvc.perform(post("/loan-decision/application")
        .contentType(APPLICATION_JSON))
      .andExpect(status().is4xxClientError());
  }

}
