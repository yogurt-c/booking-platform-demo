package io.yugurt.booking_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "io.yugurt.booking_platform.repository.nosql")
public class MongoConfig {

}
