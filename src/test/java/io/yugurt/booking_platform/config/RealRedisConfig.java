package io.yugurt.booking_platform.config;

import jakarta.annotation.PreDestroy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * 동시성 테스트 전용 실제 Redis 설정
 * Testcontainers로 실제 Redis 컨테이너를 띄워 분산 락 동작 검증
 */
@TestConfiguration
public class RealRedisConfig {

    private static GenericContainer<?> redisContainer;

    @Bean
    public GenericContainer<?> redisContainer() {
        if (redisContainer == null) {
            redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
                .withExposedPorts(6379);
            redisContainer.start();
        }
        return redisContainer;
    }

    @Bean
    public RedissonClient redissonClient(GenericContainer<?> redisContainer) {
        Config config = new Config();
        String address = "redis://" + redisContainer.getHost() + ":" + redisContainer.getMappedPort(6379);
        config.useSingleServer()
            .setAddress(address)
            .setTimeout(3000)
            .setConnectionPoolSize(10)
            .setConnectionMinimumIdleSize(5);

        return Redisson.create(config);
    }

    @PreDestroy
    public void cleanup() {
        if (redisContainer != null && redisContainer.isRunning()) {
            redisContainer.stop();
        }
    }
}
