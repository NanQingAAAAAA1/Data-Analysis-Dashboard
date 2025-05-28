package com.cg.controller;


import com.cg.pojo.Result;
import com.cg.pojo.User;
import com.cg.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.RestController;


@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (loginService.selectByName(user.getUsername()) != null){
            return Result.success(loginService.selectByName(user.getUsername()),"登录成功");
        }
        return Result.error("登录失败");
    }


}