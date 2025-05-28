package com.cg.pojo;

import lombok.Data;

@Data
public class JobTitleCount {
    private String jobTitle;
    private Integer jobOpenings;

    public JobTitleCount(String jobTitle, Integer jobOpenings) {
        this.jobTitle = jobTitle;
        this.jobOpenings = jobOpenings;
    }
}
