package com.cg.service.impl;

import com.cg.dao.CityLinkedSalaryChartDTO;
import com.cg.mapper.LoadChartMapper;
import com.cg.pojo.CityJobSummary;
import com.cg.pojo.JobTitleCount;
import com.cg.service.LoadChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class LoadChartServiceImpl implements LoadChartService {

    @Autowired
    private LoadChartMapper loadChartMapper;

    /**
     * 获取城市平均工资
     * @return 存储每个城市平均工资的列表
     */
    @Override
    public List<HashMap<String, Object>> getCityAverageSalary() {
        if(loadChartMapper.getCityAverageSalary() != null){
            return loadChartMapper.getCityAverageSalary();
        }
        return null;
    }

    /**
     * 获取技能名称和 occurrence_count
     * @return 存储技能名称和 occurrence_count 的列表
     */
    @Override
    public List<HashMap<String, Object>> getSkillNameAndOccurrenceCount() {
        return loadChartMapper.getSkillNameAndOccurrenceCount();
    }

    /**
     * 获取教育水平与职位数量的关系
     * @return 存储教育水平与职位数量的列表
     */
    @Override
    public List<HashMap<String, Object>> getEducationLevelAndJobCount() {
        return loadChartMapper.getEducationLevelAndJobCount();
    }

    /**
     * 获取学历与平均工资的关系
     * @return 存储学历与平均工资的列表
     */

    @Override
    public List<HashMap<String, Object>> getEducationLevelAndAvgSalary() {
        return loadChartMapper.getEducationLevelAndAvgSalary();
    }

    /**
     * 获取城市、职位名称和职位数量
     * @return 存储城市、职位名称和职位数量的列表
     */
//    @Override
//    public List<HashMap<String, Object>> getCityAndJobTitleAndJobOpenings() {
//        List<HashMap<String, Object>> list = new ArrayList<>();
//        if (loadChartMapper.getCityAndJobTitleAndJobOpenings() != null){
//            HashMap<String, Object> map = new HashMap<>();
//            List<String> title = new ArrayList<>();
//            List<String> openings = new ArrayList<>();
//            for(HashMap<String, Object> m : loadChartMapper.getCityAndJobTitleAndJobOpenings()){
//                if (map.containsValue(m.get("city_name"))){
//                    title.add(m.get("job_title").toString());
//                    openings.add(m.get("job_openings").toString());
//                    map.put("job_title",title);
//                    map.put("job_openings",openings);
//                }else {
//                    if(!map.isEmpty()){
//                        list.add(map);
//                        map.clear();
//                        title.clear();
//                        openings.clear();
//                    }else{
//                        map.put("city_name",m.get("city_name"));
//                        title.add(m.get("job_title").toString());
//                        openings.add(m.get("job_openings").toString());
//                        map.put("job_title",title);
//                        map.put("job_openings",openings);
//                    }
//                }
//            }
//            return list;
//        }
//        return null;
//    }
    @Override
    public List<CityJobSummary> getCityAndJobTitleAndJobOpenings() {
        List<HashMap<String, Object>> rawData = loadChartMapper.getCityAndJobTitleAndJobOpenings();
        List<CityJobSummary> result = new ArrayList<>();

        if (rawData == null || rawData.isEmpty()) {
            return result;
        }

        CityJobSummary currentSummary = null;

        for (HashMap<String, Object> row : rawData) {
            String cityName = (String) row.get("city_name");
            String jobTitle = (String) row.get("job_title");
            Long jobOpenings = (Long) row.get("job_openings");

            if (currentSummary == null || !currentSummary.getCityName().equals(cityName)) {
                currentSummary = new CityJobSummary(cityName);
                result.add(currentSummary);
            }

            currentSummary.getJobList().add(new JobTitleCount(jobTitle, jobOpenings != null ? jobOpenings.intValue() : 0));
        }

        return result;
    }

    /**
     * 获取top10城市的薪资
     * @return top10城市的薪资的列表
     */
    @Override
    public List<HashMap<String, Object>> getTop10CitySalary() {
        return loadChartMapper.getTop10CitySalary();
    }

    @Override
    public List<CityLinkedSalaryChartDTO>getNorthSouthGuangdongShenzhenAndExperience() {

        return loadChartMapper.getNorthSouthGuangdongShenzhenAndExperience();
    }

    /**
     * 获取top10公司
     * @return top10公司的列表
     */
    @Override
    public List<HashMap<String, Object>> getTop10Company() {
        return loadChartMapper.getTop10Company();
    }
}
