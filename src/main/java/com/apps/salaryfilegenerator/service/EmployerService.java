package com.apps.salaryfilegenerator.service;

import com.apps.salaryfilegenerator.entity.Employer;

import java.util.List;

public interface EmployerService {

    public Employer getSimplifiedEmployer();

    public Employer getWpsEmployer();

    public Employer getMinistriesEmployer();

    public List<Employer> getAll();

    public Employer getById(Long id);

    public void add(Employer employee);

    public void delete(Long employeeId);

    public void update(Employer employee);

}
