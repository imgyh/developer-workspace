package com.imgyh.annotation;

import com.imgyh.enums.LimitType;

import java.lang.annotation.*;

/**
 * @Description : 限流注解
 * @Author : imgyh
 * @Date : 2024/1/29 11:49
 * @Version : v1.0
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 支持重复注解
@Repeatable(value = RateLimiters.class)
public @interface RateLimiter {

    /**
     * 限流键前缀
     *
     * @return
     */
    String key() default "rate_limit:";

    /**
     * 限流规则
     *
     * @return
     */
    RateLimitRule[] rules() default {};

    /**
     * 限流类型
     *
     * @return
     */
    LimitType type() default LimitType.DEFAULT;
}
