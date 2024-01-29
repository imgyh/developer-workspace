package com.imgyh.controller;

import com.imgyh.annotation.RateLimitRule;
import com.imgyh.annotation.RateLimiter;
import com.imgyh.enums.LimitType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description :
 * @Author : imgyh
 * @Date : 2024/1/29 20:09
 * @Version : v1.0
 **/
@RestController
public class TestController {
    @RateLimiter(rules = {@RateLimitRule, @RateLimitRule(time = 10, count = 50)})
    @RateLimiter(rules = {@RateLimitRule(time = 1, count = 2)}, type = LimitType.USER)
    @GetMapping("/test")
    public String test() {
        return "hello!";
    }
}
