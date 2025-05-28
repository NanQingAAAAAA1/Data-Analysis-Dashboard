package com.cg.mapper;


import com.cg.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface LoginMapper {

    /**
     * 登录
     * @return
     */
    @Select("select name,number from goods")
    List<HashMap<String,Object>> login();

    /**
     * 通过用户名查找密码
     * @param username
     * @return 存在返回User对象，否则返回null
     */
    @Select("select * from user where username=#{username} ")
    User selectByName(String username);

}
