package br.company.loan.service.factory;

import br.company.loan.controller.exception.InvalidIdentifierException;

import java.util.regex.Pattern;

public class ValidateUniversityStudent implements IdentifierValidType {

    private static final Pattern UNIVERSITY_PATTERN = Pattern.compile("\\d{8}");

    @Override
    public void validate(String identifier) {
        if (identifier == null
                || !UNIVERSITY_PATTERN.matcher(identifier).matches()
                || ((identifier.charAt(0) - '0') + (identifier.charAt(identifier.length() - 1) - '0') != 9)) {
            throw new InvalidIdentifierException("Invalid University Student");
        }
    }
}
