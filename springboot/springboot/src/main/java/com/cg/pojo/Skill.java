package com.cg.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Skill {
    private Integer id;
    private String name;
    private String category;
    private String createBy;
    private LocalDateTime createTime;
}

