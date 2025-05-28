package com.cg.mapper;


import com.cg.dao.CityLinkedSalaryChartDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface LoadChartMapper {

    /**
     * 获取城市平均工资
     * @return 存储每个城市平均工资的列表
     */
    @Select("select name as cityName, sum(min_salary) / count(*) as avgSalary from dict_city right join position on dict_city.id = position.city_id group by dict_city.id;")
    List<HashMap<String,Object>> getCityAverageSalary();

    /**
     * 获取技能名称和该技能出现的次数
     * @return 技能名称和该技能出现的次数（字典存储）
     */

    @Select("SELECT\n" +
            "    s.name AS skill_name,\n" +
            "    COUNT(ps.position_id) AS occurrence_count\n" +
            "FROM\n" +
            "    position_skill ps\n" +
            "        JOIN\n" +
            "    skill s ON ps.skill_id = s.id\n" +
            "GROUP BY\n" +
            "    s.name\n" +
            "ORDER BY\n" +
            "    occurrence_count DESC\n" +
            "LIMIT 10;")
    List<HashMap<String,Object>> getSkillNameAndOccurrenceCount();

    /**
     * 学历在每个职位中的占比（学历要求分布）
     * @return 学历在每个职位中的占比（字典存储）
     */

    @Select("SELECT\n" +
            "    e.name AS education_level,\n" +
            "    COUNT(p.id) AS job_count\n" +
            "FROM\n" +
            "    position p\n" +
            "        JOIN\n" +
            "    dict_education e ON p.education_id = e.id\n" +
            "GROUP BY\n" +
            "    e.name\n" +
            "ORDER BY\n" +
            "    job_count DESC;")
    List<HashMap<String,Object>> getEducationLevelAndJobCount();


    /**
     * 获取每个学历的平均薪资
     * @return 存储每个学历的平均薪资的列表
     */

    @Select("SELECT\n" +
            "   e.name AS education_level,\n" +
            "   ROUND(AVG((p.min_salary + p.max_salary) / 2), 2) AS avg_salary\n" +
            "   FROM\n" +
            "   position p\n" +
            "       JOIN\n" +
            "   dict_education e ON p.education_id = e.id\n" +
            "   GROUP BY\n" +
            "   e.name\n" +
            "   ORDER BY\n" +
            "   avg_salary DESC;")
    List<HashMap<String,Object>> getEducationLevelAndAvgSalary();

    /**
     * 获取每个城市的每个职位需求量
     * @return 存储每个城市的每个职位需求量的列表
     */
    @Select("SELECT \n" +
            "    c.name AS city_name,\n" +
            "    p.title AS job_title,\n" +
            "    COUNT(p.id) AS job_openings\n" +
            "FROM \n" +
            "    position p\n" +
            "LEFT JOIN \n" +
            "    dict_city c ON p.city_id = c.id\n" +
            "WHERE \n" +
            "    LOWER(p.title) LIKE '%全栈%' \n" +
            "    OR LOWER(p.title) LIKE '%前端%'\n" +
            "    OR LOWER(p.title) LIKE '%javascript%'\n" +
            "    OR LOWER(p.title) LIKE '%java%'\n" +
            "    OR LOWER(p.title) LIKE '%Java%'\n" +
            "    OR LOWER(p.title) LIKE '%JAVA%'\n" +
            "    OR LOWER(p.title) LIKE '%golang%'\n" +
            "    OR LOWER(p.title) LIKE '%go%'\n" +
            "    OR LOWER(p.title) LIKE '%python%'\n" +
            "         OR LOWER(p.title) LIKE '%测试%'\n" +
            "GROUP BY \n" +
            "    c.name, p.title\n" +
            "ORDER BY \n" +
            "    c.name, p.title;")
    List<HashMap<String,Object>> getCityAndJobTitleAndJobOpenings();

    /**
     * 获取top10城市的薪资
     * @return 存储top10城市的薪资的列表
     */

    @Select("SELECT\n" +
            "    c.name AS city_name,\n" +
            "    ROUND(\n" +
            "            (SUM((p.min_salary + p.max_salary) / 2) /\n" +
            "             (SELECT SUM((min_salary + max_salary) / 2) FROM position)) * 100, 2\n" +
            "    ) AS salary_percentage\n" +
            "FROM\n" +
            "    position p\n" +
            "        JOIN\n" +
            "    dict_city c ON p.city_id = c.id\n" +
            "GROUP BY\n" +
            "    c.name,\n" +
            "    (SELECT SUM((min_salary + max_salary) / 2) FROM position)\n" +
            "ORDER BY\n" +
            "    salary_percentage DESC\n" +
            "limit 10;")
    List<HashMap<String,Object>> getTop10CitySalary();

    /**
     * 获取北，上，广，深城市与经验年限
     * @return 存储北，上，广，深城市与经验年限的列表
     */

    @Select("SELECT\n" +
            "    ce.city_name as cityName,\n" +
            "    ce.experience_level as experienceLevel,\n" +
            "    ce.position_count as positionCount,\n" +
            "    (SELECT MAX(position_count) FROM (\n" +
            "                                         SELECT COUNT(*) AS position_count\n" +
            "                                         FROM position p\n" +
            "                                                  JOIN dict_city c ON p.city_id = c.id\n" +
            "                                         WHERE c.name IN ('北京', '上海', '广州', '深圳')\n" +
            "                                           AND p.min_experience IS NOT NULL\n" +
            "                                         GROUP BY c.name, p.min_experience\n" +
            "                                     ) AS subquery) as maxPositionCount\n" +
            "FROM (\n" +
            "         SELECT\n" +
            "             c.name AS city_name,\n" +
            "             p.min_experience AS experience_level,\n" +
            "             COUNT(*) AS position_count\n" +
            "         FROM\n" +
            "             position p\n" +
            "                 JOIN\n" +
            "             dict_city c ON p.city_id = c.id\n" +
            "         WHERE\n" +
            "             c.name IN ('北京', '上海', '广州', '深圳')  -- 过滤条件：仅包含北上广深\n" +
            "           AND p.min_experience IS NOT NULL\n" +
            "         GROUP BY\n" +
            "             c.name, p.min_experience\n" +
            "     ) AS ce\n" +
            "ORDER BY\n" +
            "    ce.city_name ASC, ce.experience_level ASC;")
    List<CityLinkedSalaryChartDTO> getNorthSouthGuangdongShenzhenAndExperience();

    @Select(
            "SELECT \n" +
                    "    co.name AS company_name,\n" +
                    "    COUNT(p.id) AS position_count,\n" +
                    "    COUNT(p.id) * 100.0 / (SELECT COUNT(*) FROM position) AS percentage\n" +
                    "FROM \n" +
                    "    position p\n" +
                    "JOIN \n" +
                    "    company co ON p.company_id = co.id\n" +
                    "WHERE \n" +
                    "    p.company_id IS NOT NULL\n" +
                    "GROUP BY \n" +
                    "    co.name\n" +
                    "ORDER BY \n" +
                    "    position_count DESC\n" +
                    "LIMIT 10;"
    )
    List<HashMap<String,Object>>getTop10Company();
}
