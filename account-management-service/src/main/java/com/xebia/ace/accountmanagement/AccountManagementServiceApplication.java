package com.xebia.ace.accountmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Entrypoint for the Account Management Service (SCAFFOLD-SVC-001 / AC-1).
 *
 * <p>{@link UserDetailsServiceAutoConfiguration} is excluded so Spring Boot does not generate and
 * log a default user password at startup.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class AccountManagementServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountManagementServiceApplication.class, args);
  }
}
