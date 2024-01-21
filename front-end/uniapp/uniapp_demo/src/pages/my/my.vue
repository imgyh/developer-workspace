<template>
  <view>
    我的
    <view>会员信息：{{ memberStore.profile }}</view>
    <button
        @tap="
        memberStore.setProfile({
          nickname: '程序员小郭',
          token:'12345',
        })
      "
        size="mini"
        plain
        type="primary"
    >
      保存用户信息
    </button>
    <button @tap="memberStore.clearProfile()" size="mini" plain type="warn">清理用户信息</button>
    <button @tap="getData()" size="mini" plain type="warn">测试请求</button>
  </view>

</template>

<script setup>
import useMemberStore from "@/store/modules/member";
import {request} from "@/utils/request";


let getData = () => {
  request({
    method: "GET",
    url: "/home/banner",
    header: {}
  }).then((res) => {
    console.log(res)
  })
}

let memberStore = useMemberStore()
</script>

<style lang="scss" scoped>

</style>
