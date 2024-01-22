package com.imgyh.domain;

import com.imgyh.annotation.CustomNotBlank;
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
    @CustomNotBlank(message = "密码不能为空", groups = AddGroup.class)
    private String password;
    @Min(message = "年龄要大于18", value = 18)
    private Integer age;

    public User(String userName, String password, Integer age) {
        this.userName = userName;
        this.password = password;
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                '}';
    }
}
