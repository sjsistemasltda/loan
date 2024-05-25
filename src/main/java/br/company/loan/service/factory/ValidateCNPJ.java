package br.company.loan.service.factory;

import br.company.loan.controller.exception.InvalidIdentifierException;

import java.util.regex.Pattern;

public class ValidateCNPJ implements IdentifierValidType{
    private static final Pattern CNPJ_PATTERN = Pattern.compile("\\d{14}");

    @Override
    public void validate(String identifier) {
        if (identifier == null
                || !CNPJ_PATTERN.matcher(identifier).matches()
                || identifier.chars().distinct().count() == 1
        ) {
            throw new InvalidIdentifierException("Invalid CNPJ");
        }

        int sum = 0;
        int[] weight = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 12; i++) {
            sum += (identifier.charAt(i) - '0') * weight[i];
        }
        int firstCheckDigit = sum % 11;
        firstCheckDigit = firstCheckDigit < 2 ? 0 : 11 - firstCheckDigit;

        sum = 0;
        weight = new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 13; i++) {
            sum += (identifier.charAt(i) - '0') * weight[i];
        }
        int secondCheckDigit = sum % 11;
        secondCheckDigit = secondCheckDigit < 2 ? 0 : 11 - secondCheckDigit;

        boolean isValidCNPJ = identifier.charAt(12) - '0' == firstCheckDigit && identifier.charAt(13) - '0' == secondCheckDigit;

        if(!isValidCNPJ) {
            throw new InvalidIdentifierException("Invalid CNPJ");
        }
    }
}
