package com.cg.service;

import com.cg.pojo.User;
import org.springframework.stereotype.Service;


@Service
public interface LoginService {
    /**
     * 通过用户名查找密码
     * @param username 用户名
     * @return 存在返回User对象，否则返回null
     */
    User selectByName(String username);
}
