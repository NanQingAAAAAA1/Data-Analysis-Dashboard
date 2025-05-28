package com.cg.controller;


import com.cg.mapper.ModifyMapper;
import com.cg.pojo.Result;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModifyController {

    @Autowired
    private ModifyMapper modifyMapper;

    @PostMapping("/modify")
    public Result modify(@Param("number") Integer number)
    {
        modifyMapper.updateGoodsNumber(number);
        return Result.success("修改成功");
    }
}
