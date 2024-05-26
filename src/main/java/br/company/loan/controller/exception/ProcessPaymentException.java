package br.company.loan.controller.exception;

public class ProcessPaymentException extends RuntimeException {
    public ProcessPaymentException(String message) {
        super(message);
    }
}