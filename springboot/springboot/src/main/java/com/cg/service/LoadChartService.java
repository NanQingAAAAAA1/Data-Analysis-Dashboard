package com.cg.service;


import com.cg.dao.CityLinkedSalaryChartDTO;
import com.cg.pojo.CityJobSummary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public interface LoadChartService {

    /**
     * 获取城市平均工资
     * @return 存储每个城市平均工资的列表
     */
    List<HashMap<String,Object>> getCityAverageSalary();

    /**
     * 获取技能名称和该技能出现的次数
     * @return 技能名称和该技能出现的次数（字典存储）
     */
    List<HashMap<String,Object>> getSkillNameAndOccurrenceCount();

    /**
     * 获取学历和该学历下工作的数量
     * @return 学历和该学历下工作的数量（字典存储）
     */
    List<HashMap<String,Object>> getEducationLevelAndJobCount();

    /**
     * 获取学历和该学历下平均工资
     * @return 学历和该学历下平均工资（字典存储）
     */
    List<HashMap<String,Object>> getEducationLevelAndAvgSalary();

    /**
     * 获取城市和职位名称和职位数量
     * @return 城市和职位名称和职位数量（字典存储）
     */
//    List<HashMap<String,Object>> getCityAndJobTitleAndJobOpenings();
    List<CityJobSummary> getCityAndJobTitleAndJobOpenings();

    /**
     * 获取top10城市的薪资
     * @return 存储top10城市的薪资的列表
     */
    List<HashMap<String,Object>> getTop10CitySalary();

    /**
     * 获取北上广深 Experience
     * @return 存储北上广深 Experience 的列表
     */
    List<CityLinkedSalaryChartDTO>getNorthSouthGuangdongShenzhenAndExperience();

    /**
     * 获取Top10公司
     * @return 存储top10公司的列表
     */
    List<HashMap<String,Object>>getTop10Company();
}
