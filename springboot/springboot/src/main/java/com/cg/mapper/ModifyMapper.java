package com.cg.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ModifyMapper {

    @Update("update goods set number = #{number} where id = 1;")
    void updateGoodsNumber(@Param("number") Integer number);
}
