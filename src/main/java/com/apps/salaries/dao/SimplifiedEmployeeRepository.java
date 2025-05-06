package com.apps.salaries.dao;

import com.apps.salaries.entity.SimplifiedEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimplifiedEmployeeRepository extends JpaRepository<SimplifiedEmployee, Long> {
}
