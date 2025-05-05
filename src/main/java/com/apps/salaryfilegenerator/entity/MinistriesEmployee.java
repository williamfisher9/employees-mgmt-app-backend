package com.apps.salaryfilegenerator.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ministries_employee")
public class MinistriesEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "employee_bank")
    private String employeeBank;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "amount", precision = 12, scale = 3 )
    private BigDecimal amount;

    @Column(name = "deductions", precision = 12, scale = 3 )
    private BigDecimal deductions;

    @Column(name = "note")
    private String note;

    public MinistriesEmployee() {
    }

    public MinistriesEmployee(String employeeName, String accountNumber, String employeeBank, String idNumber, BigDecimal amount, BigDecimal deductions, String note) {
        this.employeeName = employeeName;
        this.accountNumber = accountNumber;
        this.employeeBank = employeeBank;
        this.idNumber = idNumber;
        this.deductions=deductions;
        this.amount = amount;
        this.note = note;
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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "MinistriesEmployee{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", employeeBank='" + employeeBank + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", amount=" + amount +
                ", deductions=" + deductions +
                ", note='" + note + '\'' +
                '}';
    }
}
