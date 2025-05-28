package cn.moonshot.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Position {
    private int id;
    private String jobName; // 映射 title
    private int companyId; // 映射 company_id
    private Integer cityId; // 映射 city_id
    private Integer minExperience; // 映射 min_experience
    private Integer maxExperience; // 映射 max_experience
    private Integer educationLevelId; // 映射 education_id
    private Double minSalary; // 映射 min_salary
    private Double maxSalary; // 映射 max_salary
    private String createBy; // 映射 create_by
    private LocalDateTime createTime; // 映射 create_time
}


