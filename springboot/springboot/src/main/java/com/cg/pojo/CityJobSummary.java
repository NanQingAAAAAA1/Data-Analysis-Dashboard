package com.cg.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CityJobSummary {
    private String cityName;
    private List<JobTitleCount> jobList;

    public CityJobSummary(String cityName) {
        this.cityName = cityName;
        this.jobList = new ArrayList<>();
    }
}
