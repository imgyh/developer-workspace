package com.imgyh.annotation;

import com.imgyh.validator.CustomNoBlankValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.*;

/**
 * @Description : 自定义校验注解
 * @Author : imgyh
 * @Date : 2024/1/22 21:21
 * @Version : v1.0
 **/

@Documented
// 使用哪些校验器校验
@Constraint(
        validatedBy = {CustomNoBlankValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomNotBlank {
    // 校验失败的提示信息去哪儿取 需要把提示信息写到 ValidationMessages.properties
    String message() default "{javax.validation.constraints.NotBlank.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // 同一个元素上指定多个该注解时使用
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        NotBlank[] value();
    }
}