package com.apps.salaries.validators;

import com.apps.salaries.entity.Employer;
import com.apps.salaries.entity.MinistriesEmployee;
import com.apps.salaries.entity.SimplifiedEmployee;
import com.apps.salaries.entity.WpsEmployee;
import com.apps.salaries.enums.IdTypes;
import com.apps.salaries.enums.Banks;
import com.apps.salaries.service.EmployerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EmployeeRecordValidator {

    @Autowired
    private EmployerService employerService;

    @Value("${default.bank.account.number.length}")
    private String defaultBankAccountNumberLength;

    @Value("${default.bank.code}")
    private String defaultBankCode;

    public boolean isWpsEmployeeRecordComplete(WpsEmployee employee){
        boolean isRecordComplete = true;

        Employer employer = null;
        employer = employerService.getWpsEmployer();

        if (employer != null) {
            //if salary type, basic salary should not be 0 but net salary can be 0
            if (employer.getPaymentType() == 10) {

                if (employee.getBasicSalary().compareTo(BigDecimal.ZERO) == 0 || employee.getBasicSalary().compareTo(BigDecimal.ZERO) == -1) {
                    //System.out.println(">>>>>>> 1");
                    isRecordComplete = false;
                    return isRecordComplete;
                }

                if (employee.getBasicSalary().compareTo(new BigDecimal("999999999.999")) == 1 ) {
                    //System.out.println(">>>>>>> 2");
                    isRecordComplete = false;
                    return isRecordComplete;
                }

                if (employee.getBasicSalary().add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions()).compareTo(BigDecimal.ZERO) == -1){
                    //System.out.println(">>>>>>> 3");
                    isRecordComplete = false;
                    return isRecordComplete;
                }

            } else {
                //if non salary type, basic salary can be 0 but net salary should be greater than 0
                if (employee.getBasicSalary().compareTo(BigDecimal.ZERO) == -1) {
                    //System.out.println(">>>>>>> 4");
                    isRecordComplete = false;
                    return isRecordComplete;
                }

                if (employee.getBasicSalary().compareTo(new BigDecimal("999999999.999")) == 1 ) {
                    //System.out.println(">>>>>>> 5");
                    isRecordComplete = false;
                    return isRecordComplete;
                }

                if (employee.getBasicSalary().add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions()).compareTo(BigDecimal.ZERO) == -1
                || employee.getBasicSalary().add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions()).compareTo(BigDecimal.ZERO) == 0){
                    //System.out.println(">>>>>>> 6");
                    isRecordComplete = false;
                    return isRecordComplete;
                }

            }
        }

        //StringUtils.isBlank tests "" and " " and null
        if (StringUtils.isBlank(employee.getIdType()) || (employee.getIdType() == "C" && !StringUtils.isNumeric(employee.getIdNumber()))) {
            //System.out.println(">>>>>>> 7");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getIdType()) || (employee.getIdType() == "P" && !StringUtils.isAlphanumeric(employee.getIdNumber()))) {
            //System.out.println(">>>>>>> 8");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getAccountNumber()) || (employee.getAccountNumber().length() != Integer.valueOf(defaultBankAccountNumberLength)
                && employee.getEmployeeBank().equalsIgnoreCase(defaultBankCode))) {
            //System.out.println(">>>>>>> 9");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getEmployeeBank()) || Banks.getBankByValue(employee.getEmployeeBank()) == null) {
            //System.out.println(">>>>>>> 10");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getIdType())  || IdTypes.getIdTypeByValue(employee.getIdType()) == null) {
            //System.out.println(">>>>>>> 11");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getAccountNumber())  || employee.getAccountNumber().length() > 35) {
            //System.out.println(">>>>>>> 12");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getEmployeeName())  || employee.getEmployeeName().length() > 70
                || !StringUtils.isAlphaSpace(employee.getEmployeeName())) {
            //System.out.println(">>>>>>> 13");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getIdNumber()) ) {
            //System.out.println(">>>>>>> 14");
            isRecordComplete = false;
            return isRecordComplete;
        }

        return isRecordComplete;
    }

    public boolean isSimplifiedEmployeeRecordComplete(SimplifiedEmployee employee){

        boolean isRecordComplete = true;


        if (employee.getAmount().compareTo(BigDecimal.ZERO) == 0 || employee.getAmount().compareTo(BigDecimal.ZERO) == -1) {
            //System.out.println(">>>>>>> 1");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getAccountNumber()) || (employee.getAccountNumber().length() != Integer.valueOf(defaultBankAccountNumberLength)
                && employee.getEmployeeBank().equalsIgnoreCase(defaultBankCode))) {
            //System.out.println(">>>>>>> 9");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getEmployeeBank()) || Banks.getBankByValue(employee.getEmployeeBank()) == null) {
            //System.out.println(">>>>>>> 10");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getAccountNumber()) || employee.getAccountNumber().length() > 35) {
            //System.out.println(">>>>>>> 12");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getEmployeeName()) || employee.getEmployeeName().length() > 70
                || !StringUtils.isAlphaSpace(employee.getEmployeeName())) {
            //System.out.println(">>>>>>> 13");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (!StringUtils.isBlank(employee.getNote()) && !StringUtils.isAlphanumericSpace(employee.getNote())) {
            //System.out.println(">>>>>>> 14");
            isRecordComplete = false;
            return isRecordComplete;
        }

        return isRecordComplete;

    }

    public boolean isMinistriesEmployeeRecordComplete(MinistriesEmployee employee){
        boolean isRecordComplete = true;


        if (employee.getAmount().compareTo(BigDecimal.ZERO) == 0 || employee.getAmount().compareTo(BigDecimal.ZERO) == -1) {
            //System.out.println(">>>>>>> 1");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (employee.getAmount().subtract(employee.getDeductions()).compareTo(BigDecimal.ZERO) == 0
        || employee.getAmount().subtract(employee.getDeductions()).compareTo(BigDecimal.ZERO) == -1){
            //System.out.println(">>>>>>> 3");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getAccountNumber())  || (employee.getAccountNumber().length() != Integer.valueOf(defaultBankAccountNumberLength)
                && employee.getEmployeeBank().equalsIgnoreCase(defaultBankCode))) {
            //System.out.println(">>>>>>> 9");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getEmployeeBank())  || Banks.getBankByValue(employee.getEmployeeBank()) == null) {
            //System.out.println(">>>>>>> 10");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getAccountNumber()) || employee.getAccountNumber().length() > 35) {
            //System.out.println(">>>>>>> 12");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (StringUtils.isBlank(employee.getEmployeeName()) || employee.getEmployeeName().length() > 70 || !StringUtils.isAlphaSpace(employee.getEmployeeName())) {
            //System.out.println(">>>>>>> 15");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (!StringUtils.isBlank(employee.getIdNumber()) && (employee.getIdNumber().length() > 17 || !StringUtils.isAlphanumeric(employee.getIdNumber()))) {
            //System.out.println(">>>>>>> 13");
            isRecordComplete = false;
            return isRecordComplete;
        }

        if (!StringUtils.isBlank(employee.getNote()) && !StringUtils.isAlphanumericSpace(employee.getNote())) {
            //System.out.println(">>>>>>> 14");
            isRecordComplete = false;
            return isRecordComplete;
        }

        return isRecordComplete;
    }

}
