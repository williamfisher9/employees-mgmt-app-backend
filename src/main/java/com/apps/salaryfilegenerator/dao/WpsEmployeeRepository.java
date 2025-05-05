package com.apps.salaryfilegenerator.dao;

import com.apps.salaryfilegenerator.entity.WpsEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WpsEmployeeRepository extends JpaRepository<WpsEmployee, Long> {
}
