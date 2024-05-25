package br.company.loan.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum LoanAmount {
    PF(BigDecimal.valueOf(300.00), BigDecimal.valueOf(10_000.00)),
    PJ(BigDecimal.valueOf(1_000.00), BigDecimal.valueOf(100_000.00)),
    EU(BigDecimal.valueOf(100.00), BigDecimal.valueOf(10_000.00)),
    AP(BigDecimal.valueOf(400.00), BigDecimal.valueOf(25_000.00));

    private final BigDecimal minAmountMonthly;
    private final BigDecimal maxAmountLoan;

    public static LoanAmount getByName(String name) {
        for (LoanAmount type : LoanAmount.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant " + name);
    }
    
}
