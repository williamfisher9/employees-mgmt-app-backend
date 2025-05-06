package com.apps.salaries.service;

import com.apps.salaries.entity.MinistriesEmployee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MinistriesEmployeeService {

    public MinistriesEmployee add(MinistriesEmployee employee);

    public List<MinistriesEmployee> getAll();

    public Optional<MinistriesEmployee> findById(long id);

    public void deleteById(long id);

    public void deleteAll();

    public long getCount();

    public BigDecimal getTotalAmount();

    public List<MinistriesEmployee> findIncompleteRecords();

}
