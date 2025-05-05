package com.apps.salaryfilegenerator.dao;

import com.apps.salaryfilegenerator.entity.MinistriesEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinistriesEmployeeRepository extends JpaRepository<MinistriesEmployee, Long> {
}
