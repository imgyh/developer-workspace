package com.imgyh.controller;

import com.imgyh.domain.User;
import com.imgyh.valid.AddGroup;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description : 用户Controller
 * @Author : imgyh
 * @Date : 2024/1/22 12:39
 * @Version : v1.0
 **/
@RestController
public class UserController {

    @PostMapping("/user1")
    public Map<String, Object> add(@Validated @RequestBody User user, BindingResult result) {
        Map<String, Object> res = new HashMap<>();

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            Map<String, String> map = new HashMap<>();
            fieldErrors.forEach((item) -> {
                // FieldError 获取到错误提示
                String message = item.getDefaultMessage();
                // 获取错误的属性的名字
                String field = item.getField();
                map.put(field, message);
            });
            res.put("msg", "参数校验失败");
            res.put("data", map);
            return res;
        }

        System.out.println(user);
        res.put("msg", "处理成功");
        return res;
    }

    @PostMapping("/user2")
    public Map<String, Object> add2(@Validated @RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        System.out.println(user);
        res.put("msg", "处理成功");
        return res;
    }
    @PostMapping("/user3")
    public Map<String, Object> add3(@Validated({AddGroup.class}) @RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        System.out.println(user);
        res.put("msg", "处理成功");
        return res;
    }
}
