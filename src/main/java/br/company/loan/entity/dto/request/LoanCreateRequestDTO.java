package br.company.loan.entity.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LoanCreateRequestDTO {
    @NotNull(message = "amount is required")
    private BigDecimal amount;

    @NotNull(message = "invoiceQuantity is required")
    private Integer invoiceQuantity;

}
