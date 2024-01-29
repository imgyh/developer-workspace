package com.imgyh.annotation;

import java.lang.annotation.*;

/**
 * @Description : 限流器容器
 * @Author : imgyh
 * @Date : 2024/1/29 12:27
 * @Version : v1.0
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiters {
    RateLimiter[] value();
}
