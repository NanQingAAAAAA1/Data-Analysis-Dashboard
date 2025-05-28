<template>
  <div>
    <!--    头部-->
    <Header :user="user"/>

    <!--    主体-->
    <div style="display: flex">
      <!--      内容区域-->
      <router-view />
    </div>
  </div>
</template>

<script>
import Header from "@/components/Header";
import request from "@/utils/request";


export default {
  name: "Layout",
  components: {
    Header,
  },
  data() {
    return {
      user: {}
    }
  },
  created() {
    this.refreshUser()
  },
  methods: {
    refreshUser() {
      let userJson = sessionStorage.getItem("user");
      if (!userJson) {
        return
      }
      let userId = JSON.parse(userJson).id
      console.log(typeof userId)
      // 从后台取出更新后的最新用户信息
      request.get("/api/user/"+userId).then(res => {
        this.user = res.data
      })
    }
  }
}
</script>

<style scoped>

</style>
