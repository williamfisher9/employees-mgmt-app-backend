package com.apps.salaryfilegenerator.enums;

public enum WpsPaymentTypes {

    SALARY((short) 101, "Salary"),
    BONUS((short) 102, "Bonus"),
    ALLOWANCES((short) 104, "Allowance"),
    END_OF_SERVICE_BENEFIT((short) 105, "End of service benefit"),
    OVERTIME((short) 103, "Overtime Payment");

    private short value;
    private String description;

    private WpsPaymentTypes(short value, String description) {
        this.value = value;
        this.description = description;
    }

    public short getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static WpsPaymentTypes getPaymentTypeByValue(int value) {

        WpsPaymentTypes[] values = WpsPaymentTypes.values();
        for (WpsPaymentTypes type : values) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    public static WpsPaymentTypes getPaymentTypeByDescription(String description) {

        WpsPaymentTypes[] values = WpsPaymentTypes.values();
        for (WpsPaymentTypes type : values) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return null;
    }

}
