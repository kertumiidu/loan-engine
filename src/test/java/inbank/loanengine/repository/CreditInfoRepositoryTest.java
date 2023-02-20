package inbank.loanengine.repository;

import inbank.loanengine.domain.ApplicantCreditInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * This class runs test against H2 in memory database that is set up for whole application.
 * In production project I would run tests against database configured for testing purposes.
 * Using same in memory database is only for simplification for homework project.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CreditInfoRepositoryTest {
  @Autowired
  private CreditInfoRepository creditInfoRepository;

  @Test
  void getCreditInfoByPersonalCodeWithNoMatchingPersonalCode() {
    ApplicantCreditInfo response = creditInfoRepository.getCreditInfoByPersonalCode("49002010900");

    assertEquals("Response should be null", null, response);
  }

  @Test
  void getCreditInfoByPersonalCodeWithCreditModifier() {
    ApplicantCreditInfo response = creditInfoRepository.getCreditInfoByPersonalCode("49002010976");

    assertEquals("Personal codes do not match", "49002010976", response.getPersonalCode());
    assertEquals("Credit modifier should be 100", 100, response.getCreditModifier());
    assertEquals("Persona should not be debtor", false, response.getIsDebtor());
  }
  @Test
  void getCreditInfoByPersonalCodeDebtor() {
    ApplicantCreditInfo response = creditInfoRepository.getCreditInfoByPersonalCode("49002010965");

    assertEquals("Personal codes do not match", "49002010965", response.getPersonalCode());
    assertEquals("There should not be any credit modifier", null, response.getCreditModifier());
    assertEquals("Persona should be debtor", true, response.getIsDebtor());
  }
}
