package com.xebia.ace.accountmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

/** SCAFFOLD-SVC-001 / AC-1: datastore settings are sourced from the environment, not hardcoded. */
class EnvironmentConfigurationTest {

  private static Properties applicationYaml() {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(new ClassPathResource("application.yml"));
    return factory.getObject();
  }

  @Test
  @DisplayName("AC-1: datasource URL and credentials are required environment variables")
  void datasourceSettingsComeFromEnvironment() {
    Properties props = applicationYaml();

    assertThat(props.getProperty("spring.datasource.url")).isEqualTo("${DB_URL}");
    assertThat(props.getProperty("spring.datasource.username")).isEqualTo("${DB_USERNAME}");
    assertThat(props.getProperty("spring.datasource.password")).isEqualTo("${DB_PASSWORD}");
  }

  @Test
  @DisplayName("AC-1: server port is configurable via SERVER_PORT")
  void serverPortComesFromEnvironment() {
    assertThat(applicationYaml().getProperty("server.port")).startsWith("${SERVER_PORT");
  }
}
