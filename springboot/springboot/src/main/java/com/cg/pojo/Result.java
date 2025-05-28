package com.cg.pojo;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class Result {
    private Integer code;//编码：1为成功0为失败
    private String msg;//错误信息
    private Object data;//数据
    public static Result success(String msg){
        Result result = new Result();
        result.msg = msg;
        result.code = 1;
        return result;
    }
    public static Result success(Object object,String msg){
        Result result = new Result();
        result.msg = msg;
        result.data = object;
        result.code = 1;
        return result;
    }
    public static Result error(String msg){
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }
}
