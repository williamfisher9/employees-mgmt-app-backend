package com.apps.salaries.runner;

import com.apps.salaries.dao.AuthorityRepository;
import com.apps.salaries.dao.UserRepository;
import com.apps.salaries.entity.Authority;
import com.apps.salaries.entity.Form;
import com.apps.salaries.dao.FormRepository;
import com.apps.salaries.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class FormsBuilderRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(FormsBuilderRunner.class);

    private final FormRepository formRepository;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FormsBuilderRunner(FormRepository formRepository, AuthorityRepository authorityRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.formRepository = formRepository;
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        if(authorityRepository.findAll().isEmpty()) {
            authorityRepository.save(new Authority("ROLE_ADMIN"));
        }

        if(userRepository.findAll().isEmpty()) {
            User user = new User("admin", "fname", "lname",
                    passwordEncoder.encode("P@ssw0rd"), "2225557777");
            user.setUserCreationDate(LocalDateTime.now());
            user.setLastUpdateDate(LocalDateTime.now());
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);

            Authority authority = authorityRepository.findByAuthority("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Role was not found!"));

            Set<Authority> authSet = new HashSet<>();
            authSet.add(authority);
            user.setAuthorities(authSet);

            userRepository.save(user);
        }
    }
}