package com.apps.salaries.enums;

public enum SalaryFrequency {

    BI_WEEKLY("B", "Biweekly"),
    MONTHLY("M", "Monthly");

    private String value;
    private String description;

    private SalaryFrequency(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static SalaryFrequency getSalaryFrequencyByValue(String value) {

        SalaryFrequency[] values = SalaryFrequency.values();
        for (SalaryFrequency type : values) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    public static SalaryFrequency getSalaryFrequencyByDescription(String description) {

        SalaryFrequency[] values = SalaryFrequency.values();
        for (SalaryFrequency type : values) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return null;
    }
}
