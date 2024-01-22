package com.imgyh.exception;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description : 全局异常处理
 * @Author : imgyh
 * @Date : 2024/1/22 20:18
 * @Version : v1.0
 **/
@RestController
@ControllerAdvice
public class GlobalExceptionAdvice {
    // 1.处理 json 请求体调用接口校验失败抛出的异常
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Map<String, String> handleVaildException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        Map<String, String> map = new HashMap<>();
        // 1、获取校验的错误结果
        e.getFieldErrors().forEach((item) -> {
            // FieldError 获取到错误提示
            String message = item.getDefaultMessage();
            // 获取错误的属性的名字
            String field = item.getField();
            map.put(field, message);
        });
        return map;
    }

    // 2.处理 form data方式调用接口校验失败抛出的异常
    @ExceptionHandler(value = {BindException.class})
    public Map<String, String> handleVaildException(BindException e) {
        e.printStackTrace();
        Map<String, String> map = new HashMap<>();
        // 1、获取校验的错误结果
        e.getFieldErrors().forEach((item) -> {
            // FieldError 获取到错误提示
            String message = item.getDefaultMessage();
            // 获取错误的属性的名字
            String field = item.getField();
            map.put(field, message);
        });
        return map;
    }
}
