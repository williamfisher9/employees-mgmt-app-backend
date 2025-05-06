package com.apps.salaries.service;

import com.apps.salaries.entity.WpsEmployee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WpsEmployeeService {

    public WpsEmployee add(WpsEmployee employee);

    public List<WpsEmployee> getAll();

    public Optional<WpsEmployee> findById(long id);

    public void deleteById(long id);

    public void deleteAll();

    public long getCount();

    public BigDecimal getTotalAmount();

    public List<WpsEmployee> findIncompleteRecords();

}
