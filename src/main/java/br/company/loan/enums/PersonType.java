package br.company.loan.enums;

public enum PersonType {
    PF,
    PJ,
    EU,
    AP;

    public static PersonType getByName(String name) {
        for (PersonType type : PersonType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant " + name);
    }
}
