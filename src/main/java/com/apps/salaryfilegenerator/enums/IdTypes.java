package com.apps.salaryfilegenerator.enums;

public enum IdTypes {
    PASSPORT("P", "Passport"),
    CIVILID("C", "Civil ID");

    private String value;
    private String description;

    private IdTypes(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static IdTypes getIdTypeByValue(String value) {

        IdTypes[] values = IdTypes.values();
        for (IdTypes type : values) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    public static IdTypes getIdTypeByDescription(String description) {

        IdTypes[] values = IdTypes.values();
        for (IdTypes type : values) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return null;
    }
}
