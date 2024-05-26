package br.company.loan.controller.exception;

public class MinAmountLoanException extends RuntimeException {
    public MinAmountLoanException(String message) {
        super(message);
    }
}