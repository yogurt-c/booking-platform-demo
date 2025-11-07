package io.yugurt.booking_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "io.yugurt.booking_platform.repository.rdb")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {

}
