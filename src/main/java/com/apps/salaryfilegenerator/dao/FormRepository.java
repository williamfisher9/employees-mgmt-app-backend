package com.apps.salaryfilegenerator.dao;

import com.apps.salaryfilegenerator.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository  extends JpaRepository<Form,Long> {
}
