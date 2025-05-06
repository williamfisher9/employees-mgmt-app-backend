package com.apps.salaries.service;

import com.apps.salaries.dao.SimplifiedEmployeeRepository;
import com.apps.salaries.entity.SimplifiedEmployee;
import com.apps.salaries.validators.EmployeeRecordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimplifiedEmployeeServiceImpl implements SimplifiedEmployeeService {

    @Autowired
    private SimplifiedEmployeeRepository simplifiedEmployeeRepository;

    @Autowired
    private EmployeeRecordValidator employeeRecordValidator;

    @Override
    @Transactional
    public SimplifiedEmployee add(SimplifiedEmployee employee) {
        return simplifiedEmployeeRepository.save(employee);
    }

    @Override
    @Transactional
    public List<SimplifiedEmployee> getAll() {
        List<SimplifiedEmployee> employees = simplifiedEmployeeRepository.findAll();
        return employees;
    }

    @Override
    public Optional<SimplifiedEmployee> findById(long id) {
        Optional<SimplifiedEmployee> employee = simplifiedEmployeeRepository.findById(id);
        return employee;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        simplifiedEmployeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAll() {
        simplifiedEmployeeRepository.deleteAll();
    }

    @Override
    @Transactional
    public long getCount() {
        return simplifiedEmployeeRepository.count();
    }

    @Override
    @Transactional
    public BigDecimal getTotalAmount() {
        List<SimplifiedEmployee> employees = simplifiedEmployeeRepository.findAll();
        BigDecimal sum = BigDecimal.ZERO;

        if (employees != null) {
            for (SimplifiedEmployee employee : employees) {
                //System.out.println(">>>" + employee.getBasicSalary());
                sum = sum.add(employee.getAmount());
            }
        }

        return sum;
    }

    @Override
    @Transactional
    public List<SimplifiedEmployee> findIncompleteRecords() {
        List<SimplifiedEmployee> employees;
        List<SimplifiedEmployee> validationResult = new ArrayList<>();
        employees = getAll();
        if (!employees.isEmpty()) {
            for (SimplifiedEmployee employee : employees) {
                if (!employeeRecordValidator.isSimplifiedEmployeeRecordComplete(employee)) {
                    validationResult.add(employee);
                }
            }
        }

        return validationResult;
    }

}
