package com.apps.salaries.service;

import com.apps.salaries.dao.FormRepository;
import com.apps.salaries.entity.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FormServiceImpl implements FormService{

    @Autowired
    private FormRepository formRepository;

    @Transactional
    public List<Form> getAll() {
        List<Form> forms = formRepository.findAll();
        return forms;
    }

}
