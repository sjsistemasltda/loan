package br.company.loan.entity.dto.sqs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoanSenderDTO {
    private Long id;
}
