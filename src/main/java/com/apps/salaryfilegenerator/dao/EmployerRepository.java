package com.apps.salaryfilegenerator.dao;

import com.apps.salaryfilegenerator.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerRepository extends JpaRepository<Employer,Long> {

}
