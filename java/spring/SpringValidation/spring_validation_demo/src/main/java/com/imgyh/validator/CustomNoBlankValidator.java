package com.imgyh.validator;

import com.imgyh.annotation.CustomNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Description : CustomNotBlank校验类
 * @Author : imgyh
 * @Date : 2024/1/22 21:26
 * @Version : v1.0
 **/

// ConstraintValidator<A, T> A 校验的注解 T注解里面值的类型
public class CustomNoBlankValidator implements ConstraintValidator<CustomNotBlank, String> {
    // 初始化方法
    @Override
    public void initialize(CustomNotBlank constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    // 判断是否校验成功
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        // null 不做检验
        if (value == null) {
            return false;
        }
        if (value.contains(" ")) {
            // 校验失败
            return false;
        }
        // 校验成功
        return true;
    }
}
