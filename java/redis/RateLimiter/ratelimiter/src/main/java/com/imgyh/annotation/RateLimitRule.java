package com.imgyh.annotation;

/**
 * @Description : 限流规则
 * @Author : imgyh
 * @Date : 2024/1/29 12:01
 * @Version : v1.0
 **/
public @interface RateLimitRule {
    /**
     * 时间窗口, 单位秒
     *
     * @return
     */
    int time() default 60;

    /**
     * 允许请求数
     *
     * @return
     */
    int count() default 100;
}
