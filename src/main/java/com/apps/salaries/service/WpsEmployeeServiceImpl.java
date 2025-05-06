package com.apps.salaries.service;

import com.apps.salaries.dao.WpsEmployeeRepository;
import com.apps.salaries.entity.WpsEmployee;
import com.apps.salaries.validators.EmployeeRecordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WpsEmployeeServiceImpl implements WpsEmployeeService {

    @Autowired
    private WpsEmployeeRepository wpsEmployeeRepository;

    @Autowired
    private EmployeeRecordValidator employeeRecordValidator;

    @Override
    @Transactional
    public WpsEmployee add(WpsEmployee employee) {
        return wpsEmployeeRepository.save(employee);
    }

    @Override
    @Transactional
    public List<WpsEmployee> getAll() {
        List<WpsEmployee> employees = wpsEmployeeRepository.findAll();
        return employees;
    }

    @Override
    public Optional<WpsEmployee> findById(long id) {
        Optional<WpsEmployee> employee = wpsEmployeeRepository.findById(id);
        return employee;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        wpsEmployeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAll() {
        wpsEmployeeRepository.deleteAll();
    }

    @Override
    @Transactional
    public long getCount() {
        return wpsEmployeeRepository.count();
    }

    @Override
    @Transactional
    public BigDecimal getTotalAmount() {
        List<WpsEmployee> employees = wpsEmployeeRepository.findAll();
        BigDecimal sum = BigDecimal.ZERO;

        if (employees != null) {
            for (WpsEmployee employee : employees) {
                //System.out.println(">>>" + employee.getBasicSalary());
                sum = sum.add(employee.getBasicSalary()).add(employee.getExtraIncome()).subtract(employee.getDeductions()).subtract(employee.getSocialSecurityDeductions());
            }
        }

        return sum;
    }

    @Override
    @Transactional
    public List<WpsEmployee> findIncompleteRecords() {
        List<WpsEmployee> employees = getAll();
        List<WpsEmployee> validationResult = new ArrayList<>();
        if (!employees.isEmpty()) {
            for (WpsEmployee employee : employees) {
                if (!employeeRecordValidator.isWpsEmployeeRecordComplete(employee)) {
                    validationResult.add(employee);
                }
            }
        }
        return validationResult;
    }

}
