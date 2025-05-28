package com.cg.controller;


import com.cg.pojo.Result;
import com.cg.service.LoadChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Description: 加载图表控制器
 */

@RestController
@RequestMapping("/loadChart")
public class LoadChartController {

    @Autowired
    private LoadChartService loadChartService;

    /**
     * 获取城市平均薪资
     * @return 包含城市平均薪资的列表
     */
    @RequestMapping("/getCityAverageSalary")
    public Result getCityAverageSalary() {

        if (loadChartService.getCityAverageSalary() != null){
            return Result.success(loadChartService.getCityAverageSalary(),"获取城市平均薪资成功");
        }
        return Result.error("获取城市平均薪资失败");
    }

    /**
     * @Description: 获取技能名称和 occurrenceCount，以便前端绘制词云
     * @return 返回给前端的结果集（技能名称和出现次数）
     */
    @GetMapping("/getSkillNameAndOccurrenceCount")
    public Result getSkillNameAndOccurrenceCount()
    {
        if(loadChartService.getSkillNameAndOccurrenceCount()!=null){
            return Result.success(loadChartService.getSkillNameAndOccurrenceCount(),"获取成功");
        }
        return Result.error("获取失败");
    }

    /**
     * @Description: 获取学历和职位数量，以便前端绘制漏斗图
     * @return 获取学历和职位数量的结果集
     */
    @GetMapping("/getEducationLevelAndJobCount")
    public Result getEducationLevelAndJobCount(){
        if(loadChartService.getEducationLevelAndJobCount()!=null){
            return Result.success(loadChartService.getEducationLevelAndJobCount(),"获取成功");
        }
        return Result.error("获取失败");
    }

    /**
     * @Description: 获取学历和职位平均薪资，以便前端绘制饼图
     * @return 获取学历和职位平均薪资的结果集
     */

    @GetMapping("/getEducationLevelAndAvgSalary")
    public Result getEducationLevelAndAvgSalary(){
        if(loadChartService.getEducationLevelAndAvgSalary()!=null){
            return Result.success(loadChartService.getEducationLevelAndAvgSalary(),"获取成功");
        }
        return Result.error("获取失败");
    }

    /**
     * @Description: 获取城市、职位名称和职位数量，以便前端绘制热力图
     * @return 获取城市、职位名称和职位数量的结果集
     */
    @GetMapping("/getCityAndJobTitleAndJobOpenings")
    public Result getCityAndJobTitleAndJobOpenings(){
        if(loadChartService.getCityAndJobTitleAndJobOpenings()!=null){
            return Result.success(loadChartService.getCityAndJobTitleAndJobOpenings(),"获取成功");
        }
        return Result.error("获取失败");
    }

    /**
     * @Description: 获取top10c城市的薪资，以便前端绘制柱状图
     * @return 获取top10c城市的薪资的结果集
     */

    @GetMapping("/getTop10CitySalary")
    public Result getTop10CitySalary(){
        if(loadChartService.getTop10CitySalary()!=null){
            return Result.success(loadChartService.getTop10CitySalary(),"获取成功");
        }
        return Result.error("获取失败");
    }

    /**
     * @Description: 获取北上广深城市职位和经验年限，以便前端绘制折雷达图
     * @return 获取北上广深城市职位和经验等级的结果集
     */
    @GetMapping("/getNorthSouthGuangdongShenzhenAndExperience")
    public Result getNorthSouthGuangdongShenzhenAndExperience(){
        if(loadChartService.getNorthSouthGuangdongShenzhenAndExperience()!=null){
            return Result.success(loadChartService.getNorthSouthGuangdongShenzhenAndExperience(),"获取成功");
        }
        return Result.error("获取失败");
    }

    /**
     * @Description: 获取top10公司的列表
     * @return 获取top10公司的信息
     */
    @GetMapping("/getTop10Company")
    public Result getTop10Company() {
        if(loadChartService.getTop10Company() != null) {
            return  Result.success(loadChartService.getTop10Company(),"获取成功");
        }
        return Result.error("获取失败");
    }
}
