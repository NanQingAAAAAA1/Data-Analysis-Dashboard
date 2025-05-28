package com.cg.dao;


import lombok.Data;

@Data
public class CityLinkedSalaryChartDTO {
    private String cityName;
    private Integer experienceLevel;
    private Integer positionCount;
    private Integer maxPositionCount;
}
