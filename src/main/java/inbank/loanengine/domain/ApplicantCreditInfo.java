package inbank.loanengine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicantCreditInfo {
  private String personalCode;
  private Integer creditModifier;
  private Boolean isDebtor;
}
