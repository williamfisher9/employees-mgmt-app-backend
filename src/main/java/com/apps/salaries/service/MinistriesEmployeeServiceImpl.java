package com.apps.salaries.service;

import com.apps.salaries.dao.MinistriesEmployeeRepository;
import com.apps.salaries.entity.MinistriesEmployee;
import com.apps.salaries.validators.EmployeeRecordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MinistriesEmployeeServiceImpl implements MinistriesEmployeeService {

    @Autowired
    private MinistriesEmployeeRepository ministriesEmployeeRepository;

    @Autowired
    private EmployeeRecordValidator employeeRecordValidator;

    @Override
    @Transactional
    public MinistriesEmployee add(MinistriesEmployee employee) {
        return ministriesEmployeeRepository.save(employee);
    }

    @Override
    @Transactional
    public List<MinistriesEmployee> getAll() {
        List<MinistriesEmployee> employees = ministriesEmployeeRepository.findAll();
        return employees;
    }

    @Override
    public Optional<MinistriesEmployee> findById(long id) {
        Optional<MinistriesEmployee> employee = ministriesEmployeeRepository.findById(id);
        return employee;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        ministriesEmployeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAll() {
        ministriesEmployeeRepository.deleteAll();
    }

    @Override
    @Transactional
    public long getCount() {
        return ministriesEmployeeRepository.count();
    }

    @Override
    @Transactional
    public BigDecimal getTotalAmount() {
        List<MinistriesEmployee> employees = ministriesEmployeeRepository.findAll();
        BigDecimal sum = BigDecimal.ZERO;

        if (employees != null) {
            for (MinistriesEmployee employee : employees) {
                //System.out.println(">>>" + employee.getBasicSalary());
                sum = sum.add(employee.getAmount());
            }
        }

        return sum;
    }

    @Override
    @Transactional
    public List<MinistriesEmployee> findIncompleteRecords() {
        List<MinistriesEmployee> employees;
        employees = getAll();
        List<MinistriesEmployee> validationResult = new ArrayList<>();
        if (!employees.isEmpty()) {
            for (MinistriesEmployee employee : employees) {
                if (!employeeRecordValidator.isMinistriesEmployeeRecordComplete(employee)) {
                    validationResult.add(employee);
                }
            }
        }
        return validationResult;
    }

}
