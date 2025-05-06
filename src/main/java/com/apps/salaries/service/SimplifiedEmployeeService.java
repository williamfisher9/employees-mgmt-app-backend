package com.apps.salaries.service;

import com.apps.salaries.entity.SimplifiedEmployee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SimplifiedEmployeeService {

    public SimplifiedEmployee add(SimplifiedEmployee employee);

    public List<SimplifiedEmployee> getAll();

    public Optional<SimplifiedEmployee> findById(long id);

    public void deleteById(long id);

    public void deleteAll();

    public long getCount();

    public BigDecimal getTotalAmount();

    public List<SimplifiedEmployee> findIncompleteRecords();

}
