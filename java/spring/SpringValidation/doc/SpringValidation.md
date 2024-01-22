# 依赖导入

从`springboot-2.3`开始，校验包被独立成了一个`starter`组件，不在随`web` 组件导入，需要单独引入。

```xml
<dependencies>        
    <!--参数校验-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <!--web组件-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

# 简单参数校验

  1)、给Bean添加校验注解，并定义自己的message提示 如：@NotBlank(message = "用户名不能为空")

```java
package com.imgyh.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @Description : 用户类
 * @Author : imgyh
 * @Date : 2024/1/22 12:36
 * @Version : v1.0
 **/
public class User {
    @NotBlank(message = "用户名不能为空")
    private String userName;
    private String password;
    @Min(message = "年龄要大于18", value = 18)
    private Integer age;
}
```

  2)、开启校验功能@Valid.  效果：校验错误以后会有默认的响应；

  3)、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果

```java
package com.imgyh.controller;

import com.imgyh.domain.User;
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
}
```



# 注解标签

| 限制                      | 说明                                                         |
| ------------------------- | ------------------------------------------------------------ |
| @Null                     | 限制只能为null                                               |
| @NotNull                  | 限制必须不为null                                             |
| @AssertFalse              | 限制必须为false                                              |
| @AssertTrue               | 限制必须为true                                               |
| @DecimalMax(value)        | 限制必须为一个不大于指定值的数字                             |
| @DecimalMin(value)        | 限制必须为一个不小于指定值的数字                             |
| @Digits(integer,fraction) | 限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction |
| @Future                   | 限制必须是一个将来的日期                                     |
| @Max(value)               | 限制必须为一个不大于指定值的数字                             |
| @Min(value)               | 限制必须为一个不小于指定值的数字                             |
| @Past                     | 限制必须是一个过去的日期                                     |
| @Pattern(value)           | 限制必须符合指定的正则表达式                                 |
| @Size(max,min)            | 限制字符长度必须在min到max之间                               |
| @Past                     | 验证注解的元素值（日期类型）比当前时间早                     |
| @NotEmpty                 | 验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0） |
| @NotBlank                 | 验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格 |
| @Email                    | 验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式 |

# 全局异常处理

如果每个Controller方法中都写一遍对BindingResult信息的处理，使用起来还是很繁琐。可以通过全局异常处理的方式统一处理校验异常。

当我们写了@Valid注解，不写BindingResult的时候，Spring 就会抛出异常。由此，可以写一个全局异常处理类来统一处理这种校验异常，从而免去重复组织异常信息的代码。

全局异常处理类只需要在类上标注@RestControllerAdvice，并在处理相应异常的方法上使用@ExceptionHandler注解，写明处理哪个异常即可。

UserController：

```java
@PostMapping("/user2")
public Map<String, Object> add2(@Validated @RequestBody User user) {
    Map<String, Object> res = new HashMap<>();
    System.out.println(user);
    res.put("msg", "处理成功");
    return res;
}
```

GlobalExceptionAdvice：

```java
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
```



# 分组校验

如果同一个参数，需要在不同场景下应用不同的校验规则，就需要用到分组校验了。比如：新注册用户还没起名字，我们允许name字段为空，但是不允许将名字更新为空字符。

分组校验有三个步骤：

- 定义一个分组类（或接口）

```java
package com.imgyh.valid;

import javax.validation.groups.Default;

/**
 * @Description : 添加分组
 * @Author : imgyh
 * @Date : 2024/1/22 21:13
 * @Version : v1.0
 **/
public interface AddGroup extends Default {
}
```

- 在校验注解上添加groups属性指定分组

```java
package com.imgyh.domain;

import com.imgyh.valid.AddGroup;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @Description : 用户类
 * @Author : imgyh
 * @Date : 2024/1/22 12:36
 * @Version : v1.0
 **/
public class User {
    @NotBlank(message = "用户名不能为空", groups = AddGroup.class)
    private String userName;
    private String password;
    @Min(message = "年龄要大于18", value = 18)
    private Integer age;
}
```

- Controller方法的@Validated注解添加分组类

```java
@PostMapping("/user3")
public Map<String, Object> add3(@Validated({AddGroup.class}) @RequestBody User user) {
    Map<String, Object> res = new HashMap<>();
    System.out.println(user);
    res.put("msg", "处理成功");
    return res;
}
```

自定义的AddGroup分组接口继承了Default接口。校验注解 (如： @NotBlank和@validated) 默认都属于Default.class分组。

在编写`AddGroup`分组接口时，如果继承了`Default`，下面两个写法就是等效的：

```java
@Validated({AddGroup.class})
@Validated({AddGroup.class,Default.class})
```

如果`AddGroup`不继承`Default`，`@Validated({AddGroup.class})`就只会校验属于`AddGroup.class`分组的参数字段



# 递归校验

如果 User 类中增加一个 Order 类的属性，而 Order 中的属性也需要校验，就用到递归校验了，只要在相应属性上增加`@Valid`注解即可实现（对于集合同样适用）

Order类如下

```java
public class Order {
    @NotNull
    private Long id;
    @NotBlank(message = "itemName 不能为空")
    private String itemName;
    // 省略其他代码...
}
```

在 User 类中增加一个 Order 类型的属性

```java
public class User {
    @NotBlank(message = "name 不能为空",groups = Update.class)
    private String name;
    //需要递归校验的Order
    @Valid
    private Order order;
    // 省略其他代码...
}   
```



# 自定义校验

Spring Validation允许用户自定义校验，实现很简单，分两步：

- 自定义校验注解

```java
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
```



- 编写校验者类

```java
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

```

