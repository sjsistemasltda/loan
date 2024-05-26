package br.company.loan.controller.exception;

public class LoanMakeException extends RuntimeException {
    public LoanMakeException(String message) {
        super(message);
    }
}