package com.apps.salaries.dao;

import com.apps.salaries.entity.WpsEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WpsEmployeeRepository extends JpaRepository<WpsEmployee, Long> {
}
