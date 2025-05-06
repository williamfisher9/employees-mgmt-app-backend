package com.apps.salaries.runner;

import com.apps.salaries.entity.Form;
import com.apps.salaries.dao.FormRepository;
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
                "/salaries/detailed/dashboard",
                "Detailed Form",
                "Use this form to create a Central Bank detailed PDF and Excel salary files.",
                "table"));

        formRepository.save(new Form(2,
                "/salaries/simplified/dashboard",
                "Simplified Form",
                "Use this form to create a simplified version of the PDF and Excel salary files.",
                "draft"));

        formRepository.save(new Form(3,
                "/salaries/deductions/dashboard",
                "Deductions Form",
                "Use this form to create a deductions version of the PDF and Excel salary files.",
                "domain"));

        formRepository.save(new Form(4,
                "/salaries/support",
                "Support",
                "You can use this page if you more details or assistance.",
                "support"));

        formRepository.findAll().forEach((form) -> {
            logger.info("{}", form);
        });
    }
}