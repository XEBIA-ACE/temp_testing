package com.xebia.ace.accountmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * SCAFFOLD-SVC-001 / AC-1: the service starts against PostgreSQL using only the environment-style
 * settings ({@code DB_URL}, {@code DB_USERNAME}, {@code DB_PASSWORD}) and the health-check
 * responds.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthCheckIT {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void environment(DynamicPropertyRegistry registry) {
    registry.add("DB_URL", postgres::getJdbcUrl);
    registry.add("DB_USERNAME", postgres::getUsername);
    registry.add("DB_PASSWORD", postgres::getPassword);
  }

  @Autowired TestRestTemplate rest;

  @Autowired Flyway flyway;

  @Test
  @DisplayName("AC-1: health-check responds UP with database connected")
  void healthIsUp() {
    ResponseEntity<String> response = rest.getForEntity("/actuator/health", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("\"status\":\"UP\"");
  }

  @Test
  @DisplayName("AC-1: liveness and readiness probes respond UP")
  void probesAreUp() {
    assertThat(rest.getForEntity("/actuator/health/liveness", String.class).getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(rest.getForEntity("/actuator/health/readiness", String.class).getStatusCode())
        .isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("AC-1: Flyway migrations run on startup")
  void flywayMigrated() {
    assertThat(flyway.info().applied()).isNotEmpty();
    assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("1");
  }

  @Test
  @DisplayName("AC-1: endpoints other than the health-check are not publicly accessible")
  void otherEndpointsDenied() {
    assertThat(rest.getForEntity("/", String.class).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(rest.getForEntity("/actuator/env", String.class).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }
}
