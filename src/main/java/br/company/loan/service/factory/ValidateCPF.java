package br.company.loan.service.factory;

import br.company.loan.controller.exception.InvalidIdentifierException;

import java.util.regex.Pattern;

public class ValidateCPF implements IdentifierValidType {
    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{11}");

    @Override
    public void validate(String identifier) {
        if (identifier == null
                || !CPF_PATTERN.matcher(identifier).matches()
                || identifier.chars().distinct().count() == 1
        ) {
            throw new InvalidIdentifierException("Invalid CPF");
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (identifier.charAt(i) - '0') * (10 - i);
        }
        int firstCheckDigit = 11 - (sum % 11);
        if (firstCheckDigit >= 10) {
            firstCheckDigit = 0;
        }

        sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (identifier.charAt(i) - '0') * (11 - i);
        }
        sum += firstCheckDigit * 2;
        int secondCheckDigit = 11 - (sum % 11);
        if (secondCheckDigit >= 10) {
            secondCheckDigit = 0;
        }
        boolean isValidCPF = identifier.charAt(9) - '0' == firstCheckDigit && identifier.charAt(10) - '0' == secondCheckDigit;

        if(!isValidCPF) {
            throw new InvalidIdentifierException("Invalid CPF");
        }
    }
}
