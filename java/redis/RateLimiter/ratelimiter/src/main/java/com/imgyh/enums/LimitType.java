package com.imgyh.enums;

/**
 * @Description : 限流类型
 * @Author : imgyh
 * @Date : 2024/1/29 11:54
 * @Version : v1.0
 **/
public enum LimitType {
    /**
     * 默认策略全局限流, 根据接口限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP,

    /**
     * 根据用户限流
     */
    USER,
}
