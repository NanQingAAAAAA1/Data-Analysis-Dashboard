package com.cg.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PositionSkill {
    private Integer positionId;
    private Integer skillId;
    private String createBy;
    private LocalDateTime createTime;
}

