package io.yugurt.booking_platform.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RedisConfig {

    @Bean
    @Profile("!test")
    public RedissonClient redissonClient(
        @Value("${spring.data.redis.host:localhost}") String host,
        @Value("${spring.data.redis.port:6379}") int port
    ) {
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }
}
