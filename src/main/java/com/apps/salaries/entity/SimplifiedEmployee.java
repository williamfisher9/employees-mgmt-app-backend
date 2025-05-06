package com.apps.salaries.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "simplified_employee")
public class SimplifiedEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "employee_bank")
    private String employeeBank;

    @Column(name = "amount", precision = 12, scale = 3 )
    private BigDecimal amount;

    @Column(name = "note")
    private String note;

    public SimplifiedEmployee() {
    }

    public SimplifiedEmployee(String employeeName, String accountNumber, String employeeBank, BigDecimal amount, String note) {
        this.employeeName = employeeName;
        this.accountNumber = accountNumber;
        this.employeeBank = employeeBank;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "SimplifiedEmployee{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", employeeBank='" + employeeBank + '\'' +
                ", amount=" + amount +
                ", note='" + note + '\'' +
                '}';
    }
}
