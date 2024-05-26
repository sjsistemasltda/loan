package br.company.loan.controller.exception;

public class MaxAmountLoanException extends RuntimeException {
    public MaxAmountLoanException(String message) {
        super(message);
    }
}