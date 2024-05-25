package br.company.loan.service.factory;

import br.company.loan.controller.exception.InvalidIdentifierException;

import java.util.regex.Pattern;

public class ValidateRetired implements IdentifierValidType {

    private static final Pattern RETIRED_PATTERN = Pattern.compile("\\d{10}");

    @Override
    public void validate(String identifier) {
        if(identifier == null || !RETIRED_PATTERN.matcher(identifier).matches()) {
            throw new InvalidIdentifierException("Invalid University Student");
        }

        char lastChar = identifier.charAt(identifier.length() - 1);
        String remainingChars = identifier.substring(0, identifier.length() - 1);

        if (remainingChars.contains(String.valueOf(lastChar))) {
            throw new InvalidIdentifierException("Invalid University Student");
        }
    }
}
