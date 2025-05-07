package com.apps.salaries.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wps_employee")
public class WpsEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "employee_bank")
    private String employeeBank;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "working_days_count")
    private int numberOfWorkingDays;

    @Column(name = "extra_hours_count")
    private int numberOfExtraHours;

    @Column(name = "basic_salary", precision = 12, scale = 3)
    private BigDecimal basicSalary;

    @Column(name = "extra_income", precision = 12, scale = 3)
    private BigDecimal extraIncome;

    @Column(name = "deductions", precision = 12, scale = 3)
    private BigDecimal deductions;

    @Column(name = "social_security_deductions", precision = 12, scale = 3)
    private BigDecimal socialSecurityDeductions;

    public WpsEmployee() {
    }

    public WpsEmployee(String employeeName, String accountNumber, String employeeBank, String idType, String idNumber, int numberOfWorkingDays, int numberOfExtraHours, BigDecimal basicSalary, BigDecimal extraIncome, BigDecimal deductions, BigDecimal socialSecurityDeductions) {
        this.employeeName = employeeName;
        this.accountNumber = accountNumber;
        this.employeeBank = employeeBank;
        this.idType = idType;
        this.idNumber = idNumber;
        this.numberOfWorkingDays = numberOfWorkingDays;
        this.numberOfExtraHours = numberOfExtraHours;
        this.basicSalary = basicSalary;
        this.extraIncome = extraIncome;
        this.deductions = deductions;
        this.socialSecurityDeductions = socialSecurityDeductions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getEmployeeBank() {
        return employeeBank;
    }

    public void setEmployeeBank(String employeeBank) {
        this.employeeBank = employeeBank;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public int getNumberOfWorkingDays() {
        return numberOfWorkingDays;
    }

    public void setNumberOfWorkingDays(int numberOfWorkingDays) {
        this.numberOfWorkingDays = numberOfWorkingDays;
    }

    public int getNumberOfExtraHours() {
        return numberOfExtraHours;
    }

    public void setNumberOfExtraHours(int numberOfExtraHours) {
        this.numberOfExtraHours = numberOfExtraHours;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getExtraIncome() {
        return extraIncome;
    }

    public void setExtraIncome(BigDecimal extraIncome) {
        this.extraIncome = extraIncome;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getSocialSecurityDeductions() {
        return socialSecurityDeductions;
    }

    public void setSocialSecurityDeductions(BigDecimal socialSecurityDeductions) {
        this.socialSecurityDeductions = socialSecurityDeductions;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", employeeBank='" + employeeBank + '\'' +
                ", idType='" + idType + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", numberOfWorkingDays=" + numberOfWorkingDays +
                ", numberOfExtraHours=" + numberOfExtraHours +
                ", basicSalary=" + basicSalary +
                ", extraIncome=" + extraIncome +
                ", deductions='" + deductions + '\'' +
                ", socialSecurityDeductions='" + socialSecurityDeductions + '\'' +
                '}';
    }
}
