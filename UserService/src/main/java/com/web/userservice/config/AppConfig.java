package com.web.userservice.config;

import com.web.userservice.entity.Account;
import com.web.userservice.entity.Manager;
import com.web.userservice.enums.Position;
import com.web.userservice.repository.AccountRepository;
import com.web.userservice.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Autowired
    PasswordEncoder passwordEncoder;

    static final String SUPER_ADMIN_USER_NAME = "superadmin";
    static final String SUPER_ADMIN_PASSWORD = "superadmin";
    static final String ADMIN_USER_NAME = "admin";
    static final String ADMIN_PASSWORD = "admin";
    static final String STAFF_USER_NAME = "staff";
    static final String STAFF_PASSWORD = "staff";

    @Bean
    @ConditionalOnProperty(
            name = "spring.datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )
    ApplicationRunner applicationRunner(AccountRepository accountRepository, ManagerRepository managerRepository) {
        return args -> {
            if (accountRepository.findByUsername(SUPER_ADMIN_USER_NAME).isEmpty()) {
                Account superAdminAccount = Account.builder()
                        .username(SUPER_ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(SUPER_ADMIN_PASSWORD))
                        .status(true)
                        .build();

                superAdminAccount = accountRepository.save(superAdminAccount);

                Manager superAdmin = Manager.builder()
                        .firstName("Super")
                        .lastName("Admin")
                        .position(Position.ADMIN)
                        .account(superAdminAccount)
                        .build();

                managerRepository.save(superAdmin);
            }

            if (accountRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                // Create admin account
                Account adminAccount = Account.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .status(true)
                        .build();

                adminAccount = accountRepository.save(adminAccount);

                Manager adminManager = Manager.builder()
                        .firstName("Admin")
                        .lastName("System")
                        .position(Position.MANAGER)
                        .account(adminAccount)
                        .build();

                managerRepository.save(adminManager);
            }

            if (accountRepository.findByUsername(STAFF_USER_NAME).isEmpty()) {
                Account staffAccount = Account.builder()
                        .username(STAFF_USER_NAME)
                        .password(passwordEncoder.encode(STAFF_PASSWORD))
                        .status(true)
                        .build();

                staffAccount = accountRepository.save(staffAccount);

                Manager staff = Manager.builder()
                        .firstName("Staff")
                        .lastName("Staff")
                        .position(Position.STAFF)
                        .account(staffAccount)
                        .build();

                managerRepository.save(staff);
            }
        };
    }
}
