package com.apps.salaries.enums;

public enum Banks {

    ROUTING1( "ROUTING1", "BANK ONE","1"),
    ROUTING2( "ROUTING2", "BANK TWO","2"),
    ROUTING3( "ROUTING3", "BANK THREE","3"),
    ROUTING4( "ROUTING4", "BANK FOUR","4"),
    ROUTING5( "ROUTING5", "BANK FIVE","5"),
    ROUTING6( "ROUTING6", "BANK SIX","6"),
    ROUTING7( "ROUTING7", "BANK SEVEN","7"),
    ROUTING8( "ROUTING8", "BANK EIGHT","8"),
    ROUTING9( "ROUTING9", "BANK NINE","9");

    private String value;
    private String description;
    private String code;

    private Banks(String value, String description, String code) {
        this.value = value;
        this.description = description;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public static Banks getBankByValue(String value) {

        Banks[] values = Banks.values();
        for (Banks bank : values) {
            if (bank.getValue().equals(value)) {
                return bank;
            }
        }
        return null;
    }

    public static Banks getBankByDescription(String description) {

        Banks[] values = Banks.values();
        for (Banks bank : values) {
            if (bank.getDescription().equals(description)) {
                return bank;
            }
        }
        return null;
    }

}