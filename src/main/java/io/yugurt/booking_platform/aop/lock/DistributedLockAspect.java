package io.yugurt.booking_platform.aop.lock;

import io.yugurt.booking_platform.exception.ReservationConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "reservation:lock:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String lockKey = REDISSON_LOCK_PREFIX + createLockKey(signature, joinPoint.getArgs(), distributedLock.key());
        RLock rLock = redissonClient.getLock(lockKey);

        try {
            // lock 획득
            boolean acquired = rLock.tryLock(
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit()
            );

            if (!acquired) {

                log.warn("Failed to acquire lock: {}", lockKey);
                throw new ReservationConflictException();
            }

            // 신규 transaction으로 실행
            return aopForTransaction.proceed(joinPoint);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            throw new ReservationConflictException();
        } finally {
            try {

                if (rLock.isHeldByCurrentThread()) {

                    rLock.unlock();
                }

            } catch (IllegalMonitorStateException e) {

                log.warn("Lock already released: {}", lockKey);
            }
        }
    }

    private String createLockKey(MethodSignature signature, Object[] args, String key) {
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(signature.getMethod());

        Object lockKey = CustomSpringELParser.getDynamicValue(parameterNames, args, key);

        return lockKey.toString();
    }
}
