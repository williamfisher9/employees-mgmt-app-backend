package com.apps.salaries.service;

import com.apps.salaries.dao.EmployerRepository;
import com.apps.salaries.entity.Employer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployerServiceImpl implements EmployerService{

    @Autowired
    private EmployerRepository employerRepository;

    @Transactional
    public void setEmployerRepository(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @Override
    public Employer getSimplifiedEmployer() {
        List<Employer> employers = employerRepository.findAll();
        Employer theEmployer = null;

        for(Employer employer: employers){
            if(employer.getFormType().equalsIgnoreCase("simplified")){
                theEmployer=employer;
            }
        }

        return theEmployer;
    }

    @Override
    public Employer getWpsEmployer() {
        List<Employer> employers = employerRepository.findAll();
        Employer theEmployer = null;
        for(Employer employer: employers){
            if(employer.getFormType().equalsIgnoreCase("detailed")){
                theEmployer=employer;
            }
        }

        if(theEmployer != null){
            return theEmployer;
        }else {
            return null;
        }

    }

    @Override
    public Employer getMinistriesEmployer() {
        List<Employer> employers = employerRepository.findAll();
        Employer theEmployer = null;

        for(Employer employer: employers){
            if(employer.getFormType().equalsIgnoreCase("deductions")){
                theEmployer=employer;
            }
        }

        return theEmployer;
    }

    @Transactional
    public List<Employer> getAll() {
        List<Employer> employers = employerRepository.findAll();
        return employers;
    }

    @Transactional
    public Employer getById(Long id) {
        Optional<Employer> optEmp = employerRepository.findById(id);
        return optEmp.get();
    }

    @Transactional
    public void add(Employer employer){
        employerRepository.save(employer);
    }

    @Transactional
    public void delete(Long id){
        employerRepository.deleteById(id);
    }

    @Transactional
    public void update(Employer employer) {
        employerRepository.save(employer);
    }

}
