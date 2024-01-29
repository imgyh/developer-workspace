package com.imgyh.aspect;

import com.imgyh.annotation.RateLimitRule;
import com.imgyh.annotation.RateLimiter;
import com.imgyh.annotation.RateLimiters;
import com.imgyh.enums.LimitType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Description : 限流器切面
 * @Author : imgyh
 * @Date : 2024/1/29 12:29
 * @Version : v1.0
 **/
@Component
@Aspect
public class RateLimiterAspect {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript limitScript;


    // 定义切点，需要把RateLimiter和RateLimiters同时加进来，否则多重注解不生效
    @Pointcut("@annotation(com.imgyh.annotation.RateLimiter)")
    public void rateLimiter() {
    }

    @Pointcut("@annotation(com.imgyh.annotation.RateLimiters)")
    public void rateLimiters() {
    }

    // 定义切点之前的操作
    @Before("rateLimiter() || rateLimiters()")
    public void doBefore(JoinPoint point) {
        try {
            // 从切点获取方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            // 获取方法
            Method method = signature.getMethod();
            String name = point.getTarget().getClass().getName() + "." + signature.getName();
            // 获取日志注解
            RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
            RateLimiters rateLimiters = method.getAnnotation(RateLimiters.class);

            List<RateLimiter> limiters = new ArrayList<>();
            if (rateLimiter != null) {
                limiters.add(rateLimiter);
            }

            if (rateLimiters != null) {
                limiters.addAll(Arrays.asList(rateLimiters.value()));
            }

            if (!allowRequest(limiters, name)) {
                throw new RuntimeException("访问过于频繁，请稍候再试");
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("服务器限流异常，请稍候再试");
        }
    }

    /**
     * 是否允许请求
     *
     * @param rateLimiters 限流注解
     * @param name         方法全名
     * @return 是否放行
     */
    private boolean allowRequest(List<RateLimiter> rateLimiters, String name) {
        List<String> keys = getKeys(rateLimiters, name);
        Object[] args = getArgs(rateLimiters);
        Object res = redisTemplate.execute(limitScript, keys, args);

        return res != null && (Long) res == 1L;
    }

    /**
     * 获取限流的键
     *
     * @param rateLimiters 限流注解
     * @param name         方法全名
     * @return
     */
    private List<String> getKeys(List<RateLimiter> rateLimiters, String name) {
        List<String> keys = new ArrayList<>();

        for (RateLimiter rateLimiter : rateLimiters) {
            String key = rateLimiter.key();
            RateLimitRule[] rules = rateLimiter.rules();
            LimitType type = rateLimiter.type();

            StringBuilder sb = new StringBuilder();
            sb.append(key).append(name);

            if (LimitType.IP == type) {
                // 获取请求ip,自己实现
                String ipAddr = "IpUtils.getIpAddr()";
                sb.append("_").append(ipAddr);
            } else if (LimitType.USER == type) {
                // 获取用户id,自己实现
                Long userId = 1L;
                sb.append("_").append(userId);
            }
            for (RateLimitRule rule : rules) {
                int time = rule.time() * 1000;
                int count = rule.count();
                StringBuilder builder = new StringBuilder(sb);
                builder.append("_").append(time).append("_").append(count);
                keys.add(builder.toString());
            }
        }
        return keys;
    }

    /**
     * 获取需要的参数
     *
     * @param rateLimiters 限流注解
     * @return
     */
    private Object[] getArgs(List<RateLimiter> rateLimiters) {
        List<Object> args = new ArrayList<>();
        args.add(System.currentTimeMillis());
        for (RateLimiter rateLimiter : rateLimiters) {
            RateLimitRule[] rules = rateLimiter.rules();
            for (RateLimitRule rule : rules) {
                int time = rule.time() * 1000;
                int count = rule.count();
                args.add(time);
                args.add(count);

                args.add(UUID.randomUUID().toString());
            }
        }
        return args.toArray();
    }
}
