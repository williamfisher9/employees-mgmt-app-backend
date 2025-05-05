package com.apps.salaryfilegenerator.runner;

import com.apps.salaryfilegenerator.entity.Form;
import com.apps.salaryfilegenerator.dao.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FormsBuilderRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(FormsBuilderRunner.class);

    @Autowired
    private FormRepository formRepository;

    @Override
    public void run(String... args) throws Exception {
        formRepository.deleteAll();

        formRepository.save(new Form(1,
                "/wps-dashboard",
                "WPS",
                "Use this form to create WPS compliant PDF and Excel files.",
                "table"));

        formRepository.save(new Form(2,
                "/simplified-dashboard",
                "Corporate (Simplified)",
                "Use this form to create a simplified version of the PDF and Excel files.",
                "draft"));

        formRepository.save(new Form(3,
                "/ministries-dashboard",
                "Non-WPS (Ministries)",
                "Use this form to create a simplified version of the PDF and Excel files.",
                "domain"));

        formRepository.save(new Form(4,
                "/support",
                "Support",
                "You can use this page if you more details or assistance.",
                "support"));

        formRepository.findAll().forEach((form) -> {
            logger.info("{}", form);
        });
    }
}