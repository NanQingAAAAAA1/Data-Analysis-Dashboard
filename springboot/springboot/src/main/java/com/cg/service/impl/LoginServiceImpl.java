package com.cg.service.impl;

import com.cg.mapper.LoginMapper;
import com.cg.pojo.User;
import com.cg.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return
     */
    @Override
    public User selectByName(String username) {
        return loginMapper.selectByName(username);
    }
}
