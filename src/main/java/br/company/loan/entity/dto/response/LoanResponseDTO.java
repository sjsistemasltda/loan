package br.company.loan.entity.dto.response;

import br.company.loan.entity.Loan;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class LoanResponseDTO {

    public static LoanResponseDTO convert(Loan loan) {
        return LoanResponseDTO.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .invoiceQuantity(loan.getInvoiceQuantity())
                .paymentStatus(loan.getPaymentStatus())
                .build();
    }

    public Long id;

    public BigDecimal amount;

    public Integer invoiceQuantity;

    public String paymentStatus;
}
