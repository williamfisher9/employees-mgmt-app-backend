package com.apps.salaries.dao;

import com.apps.salaries.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository  extends JpaRepository<Form,Long> {
}
