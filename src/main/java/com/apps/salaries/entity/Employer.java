package com.apps.salaries.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "employer")
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "form_type")
    private String formType;

    @Column(name = "employer_name")
    private String employerName;

    @Column(name = "employer_cr")
    private String employerCr;

    @Column(name = "payer_cr")
    private String payerCr;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "payment_type")
    private int paymentType;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "payment_year")
    private int paymentYear;

    @Column(name = "payment_month")
    private int paymentMonth;

    @Column(name = "salary_frequency")
    private String salaryFrequency;

    @Column(name = "debtor_account_number")
    private String debtorAccountNumber;

    @Column(name = "is_delivery_required")
    private boolean deliveryRequired;

    @Column(name = "delivery_person_name")
    private String deliveryPersonName;

    @Column(name = "delivery_peerson_id")
    private String deliveryPersonId;

    public Employer() {
    }

    public Employer(String formType, String employerName, String employerCr, String payerCr, String emailAddress, String phoneNumber, int paymentType, LocalDate valueDate, int paymentYear, int paymentMonth, String salaryFrequency, String debtorAccountNumber, boolean deliveryRequired, String deliveryPersonName, String deliveryPersonId) {
        this.formType = formType;
        this.employerName = employerName;
        this.employerCr = employerCr;
        this.payerCr = payerCr;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.paymentType = paymentType;
        this.valueDate = valueDate;
        this.paymentYear = paymentYear;
        this.paymentMonth = paymentMonth;
        this.salaryFrequency = salaryFrequency;
        this.debtorAccountNumber = debtorAccountNumber;
        this.deliveryRequired = deliveryRequired;
        this.deliveryPersonName = deliveryPersonName;
        this.deliveryPersonId = deliveryPersonId;
    }

    public boolean isDeliveryRequired() {
        return deliveryRequired;
    }

    public void setDeliveryRequired(boolean deliveryRequired) {
        this.deliveryRequired = deliveryRequired;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerCr() {
        return employerCr;
    }

    public void setEmployerCr(String employerCr) {
        this.employerCr = employerCr;
    }

    public String getPayerCr() {
        return payerCr;
    }

    public void setPayerCr(String payerCr) {
        this.payerCr = payerCr;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public int getPaymentYear() {
        return paymentYear;
    }

    public void setPaymentYear(int paymentYear) {
        this.paymentYear = paymentYear;
    }

    public int getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(int paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public String getSalaryFrequency() {
        return salaryFrequency;
    }

    public void setSalaryFrequency(String salaryFrequency) {
        this.salaryFrequency = salaryFrequency;
    }

    public String getDebtorAccountNumber() {
        return debtorAccountNumber;
    }

    public void setDebtorAccountNumber(String debtorAccountNumber) {
        this.debtorAccountNumber = debtorAccountNumber;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getDeliveryPersonName() {
        return deliveryPersonName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryPersonName = deliveryPersonName;
    }

    public String getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public void setDeliveryPersonId(String deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }

    @Override
    public String toString() {
        return "Employer{" +
                "id=" + id +
                ", formType='" + formType + '\'' +
                ", employerName='" + employerName + '\'' +
                ", employerCr='" + employerCr + '\'' +
                ", payerCr='" + payerCr + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", paymentType=" + paymentType +
                ", valueDate=" + valueDate +
                ", paymentYear=" + paymentYear +
                ", paymentMonth=" + paymentMonth +
                ", salaryFrequency='" + salaryFrequency + '\'' +
                ", debtorAccountNumber='" + debtorAccountNumber + '\'' +
                ", deliveryRequired=" + deliveryRequired +
                ", deliveryPersonName='" + deliveryPersonName + '\'' +
                ", deliveryPersonId='" + deliveryPersonId + '\'' +
                '}';
    }
}
