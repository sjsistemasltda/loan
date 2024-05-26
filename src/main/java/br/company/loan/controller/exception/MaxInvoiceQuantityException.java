package br.company.loan.controller.exception;

public class MaxInvoiceQuantityException extends RuntimeException {
    public MaxInvoiceQuantityException(String message) {
        super(message);
    }
}