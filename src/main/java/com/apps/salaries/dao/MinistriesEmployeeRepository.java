package com.apps.salaries.dao;

import com.apps.salaries.entity.MinistriesEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinistriesEmployeeRepository extends JpaRepository<MinistriesEmployee, Long> {
}
