package io.yugurt.booking_platform.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 일반 테스트용 Mock RedissonClient
 * 실제 Redis 없이 테스트 가능 (빠른 실행)
 */
@TestConfiguration
public class MockRedisConfig {

    @Bean
    @Primary
    public RedissonClient mockRedissonClient() throws Exception {
        RedissonClient redissonClient = mock(RedissonClient.class);
        RLock rLock = mock(RLock.class);

        // 락 획득 항상 성공
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        return redissonClient;
    }
}
