package com.apps.salaryfilegenerator.service;

import com.apps.salaryfilegenerator.dto.PasteEventDataObject;
import com.apps.salaryfilegenerator.entity.MinistriesEmployee;
import com.apps.salaryfilegenerator.entity.SimplifiedEmployee;
import com.apps.salaryfilegenerator.entity.WpsEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PasteEventDataServiceImpl implements  PasteEventDataService{

    @Value("${default.bank.code}")
    private String defaultBankCode;

    @Autowired
    private SimplifiedEmployeeService simplifiedEmployeeService;

    @Autowired
    private WpsEmployeeService wpsEmployeeService;

    @Autowired
    private MinistriesEmployeeService ministriesEmployeeService;

    @Override
    public ResponseEntity<?> handleMinistriesPasteEventObject(PasteEventDataObject payloadObj) {

        List<MinistriesEmployee> employees = ministriesEmployeeService.getAll();
        int employeesListSize = employees.size();
        List<String> payloadList = payloadObj.getPayload();

        //remove first element of the payload array list if empty
        if(payloadList.get(0).isEmpty()){
            payloadList.remove(0);
        }

        //remove last element of the payload array list if empty
        if(payloadList.get(payloadList.size()-1).isEmpty()){
            payloadList.remove(payloadList.size()-1);
        }

        int payloadSize = payloadList.size();

        //find staring point in the employees arraylist
        int iteratorIndex = 0;
        int recordIndexInEmployees = -1;
        for (MinistriesEmployee employee : employees) {
            if (String.valueOf(employee.getId()).equals(payloadObj.getItemId())) {
                recordIndexInEmployees = iteratorIndex;
            }
            iteratorIndex++;
        }

        //if not starting point was add in the employees arraylist
        if (recordIndexInEmployees == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No record found with ID: " + payloadObj.getItemId());
        }

        //arraylist of employees records that should be eventually updated
        List<MinistriesEmployee> toBeUpdatedEmployeesList = null;
        if(payloadSize == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arraylist is empty");
        } else if (payloadSize == 1) {
            //creates an arraylist of one item only
            toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, recordIndexInEmployees + 1);
        } else {
            //creates new records in the arraylist if records in the payload are greater than the existing records
            int recordsToCreate = 0;
            if (payloadSize > (employeesListSize - recordIndexInEmployees)) {
                recordsToCreate = payloadSize - (employeesListSize - recordIndexInEmployees);
            }

            if (recordsToCreate == 0) {
                toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, recordIndexInEmployees + payloadSize);
            } else {
                toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, employees.size());
                for (int i = 0; i < recordsToCreate; i++) {
                    toBeUpdatedEmployeesList.add(new MinistriesEmployee(null, null, defaultBankCode, null, BigDecimal.ZERO, BigDecimal.ZERO, ""));
                }
            }
        }

        int indexInPayloadList = 0;
        if (payloadObj.getFieldName().equalsIgnoreCase("accountNumber")) {
            for (MinistriesEmployee employee : toBeUpdatedEmployeesList) {
                employee.setAccountNumber(payloadList.get(indexInPayloadList).trim());
                ministriesEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("employeeName")) {
            for (MinistriesEmployee employee : toBeUpdatedEmployeesList) {
                employee.setEmployeeName(payloadList.get(indexInPayloadList).toUpperCase().trim());
                ministriesEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("idNumber")) {
            for (MinistriesEmployee employee : toBeUpdatedEmployeesList) {
                employee.setEmployeeName(payloadList.get(indexInPayloadList).toUpperCase().trim());
                ministriesEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("employeeBank")) {
            for (MinistriesEmployee employee : toBeUpdatedEmployeesList) {
                employee.setEmployeeBank(payloadList.get(indexInPayloadList).toUpperCase().trim());
                ministriesEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("note")) {
            for (MinistriesEmployee employee : toBeUpdatedEmployeesList) {
                employee.setNote(payloadList.get(indexInPayloadList).trim());
                ministriesEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("amount")) {
            for (MinistriesEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    BigDecimal valueInBigDecimal = new BigDecimal(payloadList.get(indexInPayloadList).trim());
                    employee.setAmount(valueInBigDecimal);
                } catch (Exception exc){
                    employee.setAmount(BigDecimal.ZERO);
                }
                ministriesEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("deduction")) {
            for (MinistriesEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    BigDecimal valueInBigDecimal = new BigDecimal(payloadList.get(indexInPayloadList).trim());
                    employee.setDeductions(valueInBigDecimal);
                } catch (Exception exc){
                    employee.setDeductions(BigDecimal.ZERO);
                }
                ministriesEmployeeService.add(employee);
                indexInPayloadList++;
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("Paste handler finished successfully");

    }

    //======================================================================================================================================

    @Override
    public ResponseEntity<?> handleSimplifiedPasteEventObject(PasteEventDataObject payloadObj) {

        List<SimplifiedEmployee> employees = simplifiedEmployeeService.getAll();
        int employeesListSize = employees.size();
        List<String> payloadList = payloadObj.getPayload();

        //remove first element of the payload array list if empty
        if(payloadList.get(0).isEmpty()){
            payloadList.remove(0);
        }

        //remove last element of the payload array list if empty
        if(payloadList.get(payloadList.size()-1).isEmpty()){
            payloadList.remove(payloadList.size()-1);
        }

        int payloadSize = payloadList.size();

        //find staring point in the employees arraylist
        int iteratorIndex = 0;
        int recordIndexInEmployees = -1;
        for (SimplifiedEmployee employee : employees) {
            if (String.valueOf(employee.getId()).equals(payloadObj.getItemId())) {
                recordIndexInEmployees = iteratorIndex;
            }
            iteratorIndex++;
        }

        //if not starting point was add in the employees arraylist
        if (recordIndexInEmployees == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No record found with ID: " + payloadObj.getItemId());
        }

        //arraylist of employees records that should be eventually updated
        List<SimplifiedEmployee> toBeUpdatedEmployeesList = null;
        if(payloadSize == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arraylist is empty");
        } else if (payloadSize == 1) {
            //creates an arraylist of one item only
            toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, recordIndexInEmployees + 1);
        } else {
            //creates new records in the arraylist if records in the payload are greater than the existing records
            int recordsToCreate = 0;
            if (payloadSize > (employeesListSize - recordIndexInEmployees)) {
                recordsToCreate = payloadSize - (employeesListSize - recordIndexInEmployees);
            }

            if (recordsToCreate == 0) {
                toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, recordIndexInEmployees + payloadSize);
            } else {
                toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, employees.size());
                for (int i = 0; i < recordsToCreate; i++) {
                    toBeUpdatedEmployeesList.add(new SimplifiedEmployee(null, null, defaultBankCode, BigDecimal.ZERO, ""));
                }
            }
        }

        int indexInPayloadList = 0;
        if (payloadObj.getFieldName().equalsIgnoreCase("accountNumber")) {
            for (SimplifiedEmployee employee : toBeUpdatedEmployeesList) {
                employee.setAccountNumber(payloadList.get(indexInPayloadList).trim());
                simplifiedEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("employeeName")) {
            for (SimplifiedEmployee employee : toBeUpdatedEmployeesList) {
                employee.setEmployeeName(payloadList.get(indexInPayloadList).toUpperCase().trim());
                simplifiedEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("employeeBank")) {
            for (SimplifiedEmployee employee : toBeUpdatedEmployeesList) {
                employee.setEmployeeBank(payloadList.get(indexInPayloadList).toUpperCase().trim());
                simplifiedEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("note")) {
            for (SimplifiedEmployee employee : toBeUpdatedEmployeesList) {
                employee.setNote(payloadList.get(indexInPayloadList).trim());
                simplifiedEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("amount")) {
            for (SimplifiedEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    BigDecimal valueInBigDecimal = new BigDecimal(payloadList.get(indexInPayloadList).trim());
                    employee.setAmount(valueInBigDecimal);
                } catch (Exception exc){
                    employee.setAmount(BigDecimal.ZERO);
                }
                simplifiedEmployeeService.add(employee);
                indexInPayloadList++;
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("Paste handler finished successfully");

    }

    //======================================================================================================================================

    @Override
    public ResponseEntity<?> handleWpsPasteEventObject(PasteEventDataObject payloadObj) {

        List<WpsEmployee> employees = wpsEmployeeService.getAll();
        int employeesListSize = employees.size();
        List<String> payloadList = payloadObj.getPayload();

        //remove first element of the payload array list if empty
        if(payloadList.get(0).isEmpty()){
            payloadList.remove(0);
        }

        //remove last element of the payload array list if empty
        if(payloadList.get(payloadList.size()-1).isEmpty()){
            payloadList.remove(payloadList.size()-1);
        }

        int payloadSize = payloadList.size();

        //find staring point in the employees arraylist
        int iteratorIndex = 0;
        int recordIndexInEmployees = -1;
        for (WpsEmployee employee : employees) {
            if (String.valueOf(employee.getId()).equals(payloadObj.getItemId())) {
                recordIndexInEmployees = iteratorIndex;
            }
            iteratorIndex++;
        }

        //if not starting point was add in the employees arraylist
        if (recordIndexInEmployees == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No record found with ID: " + payloadObj.getItemId());
        }

        //arraylist of employees records that should be eventually updated
        List<WpsEmployee> toBeUpdatedEmployeesList = null;
        if(payloadSize == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arraylist is empty");
        } else if (payloadSize == 1) {
            //creates an arraylist of one item only
            toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, recordIndexInEmployees + 1);
        } else {
            //creates new records in the arraylist if records in the payload are greater than the existing records
            int recordsToCreate = 0;
            if (payloadSize > (employeesListSize - recordIndexInEmployees)) {
                recordsToCreate = payloadSize - (employeesListSize - recordIndexInEmployees);
            }

            if (recordsToCreate == 0) {
                toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, recordIndexInEmployees + payloadSize);
            } else {
                toBeUpdatedEmployeesList = employees.subList(recordIndexInEmployees, employees.size());
                for (int i = 0; i < recordsToCreate; i++) {
                    toBeUpdatedEmployeesList.add(new WpsEmployee(null, null, defaultBankCode, "C", null, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
                }
            }
        }

        int indexInPayloadList = 0;
        if (payloadObj.getFieldName().equalsIgnoreCase("accountNumber")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                employee.setAccountNumber(payloadList.get(indexInPayloadList).trim());
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("employeeName")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                employee.setEmployeeName(payloadList.get(indexInPayloadList).toUpperCase().trim());
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("employeeBank")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                employee.setEmployeeBank(payloadList.get(indexInPayloadList).toUpperCase().trim());
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("idType")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                employee.setIdType(payloadList.get(indexInPayloadList).toUpperCase().trim());
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("idNumber")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                employee.setIdNumber(payloadList.get(indexInPayloadList).trim());
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("numberOfWorkingDays")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    employee.setNumberOfWorkingDays(Integer.valueOf(payloadList.get(indexInPayloadList).trim()));
                } catch (Exception exc){
                    employee.setNumberOfWorkingDays(0);
                }
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("numberOfExtraHours")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    employee.setNumberOfExtraHours(Integer.valueOf(payloadList.get(indexInPayloadList).trim()));
                } catch (Exception exc){
                    employee.setNumberOfExtraHours(0);
                }
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("basicSalary")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    BigDecimal valueInBigDecimal = new BigDecimal(payloadList.get(indexInPayloadList).trim());
                    employee.setBasicSalary(valueInBigDecimal);
                } catch (Exception exc){
                    employee.setBasicSalary(BigDecimal.ZERO);
                }
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("extraIncome")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    BigDecimal valueInBigDecimal = new BigDecimal(payloadList.get(indexInPayloadList).trim());
                    employee.setExtraIncome(valueInBigDecimal);
                } catch (Exception exc){
                    employee.setExtraIncome(BigDecimal.ZERO);
                }
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("deductions")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    BigDecimal valueInBigDecimal = new BigDecimal(payloadList.get(indexInPayloadList).trim());
                    employee.setDeductions(valueInBigDecimal);
                } catch (Exception exc){
                    employee.setDeductions(BigDecimal.ZERO);
                }
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        } else if (payloadObj.getFieldName().equalsIgnoreCase("socialSecurityDeductions")) {
            for (WpsEmployee employee : toBeUpdatedEmployeesList) {
                try {
                    BigDecimal valueInBigDecimal = new BigDecimal(payloadList.get(indexInPayloadList).trim());
                    employee.setSocialSecurityDeductions(valueInBigDecimal);
                } catch (Exception exc){
                    employee.setSocialSecurityDeductions(BigDecimal.ZERO);
                }
                wpsEmployeeService.add(employee);
                indexInPayloadList++;
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("Paste handler finished successfully");

    }

}
