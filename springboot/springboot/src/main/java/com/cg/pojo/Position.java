package com.cg.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Position {
    private Integer id;
    private String title;
    private Integer companyId;
    private Integer cityId;
    private Integer minExperience;
    private Integer maxExperience;
    private Integer educationId;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String createBy;
    private LocalDateTime createTime;
}
