package com.apps.salaries.dao;

import com.apps.salaries.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerRepository extends JpaRepository<Employer,Long> {

}
