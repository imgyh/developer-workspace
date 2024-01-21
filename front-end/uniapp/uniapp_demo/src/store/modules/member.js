//创建用户相关的小仓库
import {defineStore} from 'pinia'
//创建用户小仓库
const useMemberStore = defineStore('member', {
        //小仓库存储数据地方
        state: () => {
            return {
                profile: null,
            }
        },
        //处理异步|逻辑地方
        actions: {
            // 保存会员信息，登录时使用
            setProfile(val) {
                this.profile = val
            },
            // 清理会员信息，退出时使用
            clearProfile() {
                this.profile = null
            },
        },
        getters: {},
    },
    {
        // 配置持久化
        persist: {
            // 调整为兼容多端的API
            storage: {
                setItem(key, value) {
                    uni.setStorageSync(key, value)
                },
                getItem(key) {
                    return uni.getStorageSync(key)
                },
            },
        },
    },
)

//对外暴露小仓库
export default useMemberStore