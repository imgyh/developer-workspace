/**
 * 添加拦截器:
 *   拦截 request 请求
 *   拦截 uploadFile 文件上传
 *
 *   1. 非 http 开头拼接地址
 *   2. 请求超时
 *   3. 添加小程序端请求头标识
 *   4. 添加 token 请求头标识
 */

import useMemberStore from "@/store/modules/member";

const baseURL = import.meta.env.VITE_APP_SERVER + import.meta.env.VITE_APP_BASE_API

// 添加拦截器
const httpInterceptor = {
    // 拦截前触发
    invoke(options) {
        // 1. 非 http 开头需拼接地址
        if (!options.url.startsWith('http')) {
            options.url = baseURL + options.url
        }
        // 2. 请求超时, 默认 60s
        options.timeout = 5000
        // 3. 添加小程序端请求头标识
        options.header = {
            ...options.header,
            'source-client': 'miniapp',
        }
        // 4. 添加 token 请求头标识
        const memberStore = useMemberStore()
        const token = memberStore.profile?.token
        if (token) {
            options.header.Authorization = token
        }
    },
}
uni.addInterceptor('request', httpInterceptor)
uni.addInterceptor('uploadFile', httpInterceptor)

/**
 * 请求函数
 * @param  UniApp.RequestOptions
 * @returns Promise
 *  1. 返回 Promise 对象
 *  2. 获取数据成功 提取核心数据 res.data
 *  3. 获取数据失败
 *    3.1 401错误  -> 清理用户信息，跳转到登录页
 *    3.2 其他错误 -> 根据后端错误信息轻提示
 *    3.3 网络错误 -> 提示用户换网络
 */

export const request = (options) => {
    // 1. 返回 Promise 对象
    return new Promise((resolve, reject) => {
        uni.request({
            ...options,
            // 响应成功
            success(res) {
                // 状态码 2xx， axios 就是这样设计的
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    // 2 提取核心数据 res.data
                    resolve(res.data)
                } else if (res.statusCode === 401) {
                    // 401错误  -> 清理用户信息，跳转到登录页
                    const memberStore = useMemberStore()
                    memberStore.clearProfile()
                    uni.navigateTo({url: '/pages/login/login'})
                    reject(res)
                } else {
                    // 其他错误 -> 根据后端错误信息轻提示
                    uni.showToast({
                        icon: 'none',
                        title: res.data?.msg || '请求错误',
                    })
                    reject(res)
                }
            },
            // 响应失败
            fail(err) {
                uni.showToast({
                    icon: 'none',
                    title: '网络错误，换个网络试试',
                })
                reject(err)
            },
        })
    })
}
