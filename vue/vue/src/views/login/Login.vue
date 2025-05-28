<template>
  <div id="login-container">
    <div style="text-align: center;height: 50px">
      登录
    </div>
    <el-form :model="ruleForm" status-icon :rules="rules" ref="Form" label-width="100px" class="demo-ruleForm">
      <el-form-item label="账号" prop="username">
        <el-input type="text" prefix-icon="User" v-model="ruleForm.username" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" prefix-icon="Lock" v-model="ruleForm.password" autocomplete="off"></el-input>
      </el-form-item>
<!--      <el-form-item>
      <el-radio v-model="ruleForm.role" :label="1" style="color: white">管理员</el-radio>
      <el-radio v-model="ruleForm.role" :label="2" style="color: white">普通用户</el-radio>
    </el-form-item>-->
      <el-form-item>
        <el-button style="margin-left: 25%" type="primary" @click="login" >登录</el-button>
      </el-form-item>

    </el-form>
  </div>
</template>

<script>
  import axios from 'axios'
  // import request from "@/utils/request";

  export default {
    name: "Login",
  data() {
    return {
      ruleForm: {
        username: '',
        password: '',
      },
      rules: {
        username: [
            { required: true, message: '请输入用户名', trigger: 'blur' },
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
        ],
      },
      isLogin:false
    };
  },
  methods: {
    login() {
      //console.log(this.ruleForm)
      return axios.post('http://localhost:9090/login', {
        username: this.ruleForm.username,
        password: this.ruleForm.password,
      })
        .then(response => {
          console.log(response.data.data);
          console.log(response.data.code);
          if (response.data.code === 1) {
            sessionStorage.setItem('token', response.data.data.token);
            sessionStorage.setItem('username', response.data.data.username);
            sessionStorage.setItem('role', response.data.data.role);
            //重定向路由
            this.$router.push({path: '/'});
          }
        })
    },
    }
  }
</script>

<style scoped>
  body{
    margin: 0;
  }
  #login-container{
    width: 400px;
    height: 290px;
    background: #e5e9f2;
    position: absolute;
    left: 50%;
    top: 50%;
    margin-left: -220px;
    margin-top: -170px;
    border-radius: 5px;
    padding-top: 40px;
    padding-right: 40px;
  }
</style>
