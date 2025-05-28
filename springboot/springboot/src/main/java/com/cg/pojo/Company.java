package com.cg.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Company {
    private Integer id;
    private String name;
    private LocalDateTime createTime;
}

