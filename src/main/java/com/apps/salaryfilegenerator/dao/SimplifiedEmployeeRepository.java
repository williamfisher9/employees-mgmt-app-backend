package com.apps.salaryfilegenerator.dao;

import com.apps.salaryfilegenerator.entity.SimplifiedEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimplifiedEmployeeRepository extends JpaRepository<SimplifiedEmployee, Long> {
}
