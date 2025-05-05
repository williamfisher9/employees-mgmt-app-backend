package com.apps.salaryfilegenerator.enums;

public enum OtherPaymentTypes {

    SALARY((short) 10, "Salary"),
    BONUS((short) 11, "Bonus"),
    ALLOWANCES((short) 12, "Allowance"),
    OTHERS((short) 13, "Others"),
    DIVIDEND((short) 14, "Dividend"),
    END_OF_SERVICE_BENEFIT((short) 15, "End of service benefit"),
    OVERTIME((short) 16, "Overtime Payment"),
    MONTHLY_PENSION_SALARY((short) 17, "Monthly pension salary"),
    CHARITY_PAYMENT((short) 18, "Charity Payment"),
    MONTHLY_SOCIAL_Security_SALARY((short) 19, "Monthly Social Security Salary"),
    SOCIAL_SECURITY_ONETIME_PAYMENT((short) 20, "Social Security onetime payment"),
    DEPOSIT_OPERATION((short) 21, "Deposit Operation"),
    ACCOUNT_MANAGEMENT((short) 22, "Account Management"),
    CUSTOMER_TO_COOPERATE((short) 23, "Customer to Cooperate"),
    CUSTOMER_TO_CUSTOMER((short) 24, "Customer to Customer"),
    REVERSAL_OF_DEBIT_CREDIT_TRANSACTION((short) 25, "Reversal of Debit/ Credit Transaction"),
    COURT_ORDER((short) 26, "Court Order"),
    Government_Payment((short) 27, "Government Payment"),
    GOVERNMENT_FEE_COLLECTION((short) 28, "Government Fee Collection"),
    GOVERNMENT_PENALTY_PAYMENT((short) 29, "Government Penalty Payment"),
    PROPERTY_TAX((short) 30, "Property Tax"),
    COOPERATE_TAX((short) 31, "Cooperate Tax"),
    INCOME_TAX((short) 32, "Income Tax"),
    VALUE_ADDED_TAX((short) 33, "Value added Tax"),
    IMPORT_EXPORT_TAX((short) 34, "Import / Export Tax"),
    MUNICIPALITY_TAX((short) 35, "Municipality Tax"),
    TOURISM_TAX((short) 36, "Tourism Tax"),
    IPO_SUBSCRIPTION((short) 37, "IPO subscription"),
    IPO_REFUND((short) 38, "IPO refund"),
    MARKET_SHARES_PAYMENT((short) 39, "Market Shares Payment"),
    INTEREST_PAYMENT((short) 40, "Interest Payment"),
    LOAN_DISBURSEMENT((short) 41, "Loan Disbursement"),
    FULL_AND_FINAL_LOAN_SETTLEMENT((short) 42, "Full & final loan settlement"),
    LOAN_INSTALLMENT_PAYMENT((short) 43, "Loan installment payment"),
    LOAN_INSURANCE_REFUND((short) 44, "Loan insurance refund"),
    MORTGAGE_INSTALLMENT((short) 45, "Mortgage installment"),
    CREDIT_CARD_PAYMENT((short) 46, "Credit card payment"),
    UTILITY_PAYMENT((short) 47, "Utility payment"),
    BILLS_TO_VENDOR_COMPANY((short) 48, "Bills to vendor/ company"),
    SERVICE_PAYMENT((short) 49, "Service payment"),
    INSURANCE_PAYMENT((short) 50, "Insurance payment"),
    RENT_OF_LEASE((short) 51, "Rent of lease"),
    FEES_COLLECTION((short) 52, "Fees collection"),
    SCHOOL_FEES((short) 53, "School ees"),
    MEMBERSHIP_FEES((short) 54, "Membership fees");

    private short value;
    private String description;

    private OtherPaymentTypes(short value, String description) {
        this.value = value;
        this.description = description;
    }

    public short getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static OtherPaymentTypes getPaymentTypeByValue(int value) {

        OtherPaymentTypes[] values = OtherPaymentTypes.values();
        for (OtherPaymentTypes type : values) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    public static OtherPaymentTypes getPaymentTypeByDescription(String description) {

        OtherPaymentTypes[] values = OtherPaymentTypes.values();
        for (OtherPaymentTypes type : values) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return null;
    }

}
