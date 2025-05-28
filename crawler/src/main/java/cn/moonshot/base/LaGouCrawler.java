package cn.moonshot.base;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.moonshot.util.Dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LaGouCrawler {
    // 城市代码映射表
    private static final Map<String, Integer> CITY_CODES = new HashMap<>() {{
        // 直辖市
        put("北京", 1100);
        put("上海", 3100);
        put("天津", 1200);
        put("重庆", 5000);

        // 广东省
        put("广州", 4401);
        put("深圳", 4403);
        put("珠海", 4404);
        put("佛山", 4406);
        put("东莞", 4419);
        put("中山", 4420);

        // 浙江省
        put("杭州", 3301);
        put("宁波", 3302);
        put("温州", 3303);
        put("绍兴", 3306);
        put("嘉兴", 3304);

        // 江苏省
        put("南京", 3201);
        put("苏州", 3205);
        put("无锡", 3202);
        put("常州", 3204);
        put("南通", 3206);

        // 四川省
        put("成都", 5101);
        put("绵阳", 5107);
        put("德阳", 5106);

        // 湖北省
        put("武汉", 4201);
        put("宜昌", 4205);
        put("襄阳", 4206);

        // 陕西省
        put("西安", 6101);
        put("咸阳", 6104);

        // 福建省
        put("福州", 3501);
        put("厦门", 3502);
        put("泉州", 3505);

        // 山东省
        put("济南", 3701);
        put("青岛", 3702);
        put("烟台", 3706);

        // 辽宁省
        put("沈阳", 2101);
        put("大连", 2102);

        // 其他省会城市
        put("长沙", 4301);  // 湖南
        put("郑州", 4101);  // 河南
        put("合肥", 3401);  // 安徽
        put("南昌", 3601);  // 江西
        put("石家庄", 1301); // 河北
        put("太原", 1401);  // 山西
        put("哈尔滨", 2301); // 黑龙江
        put("长春", 2201);  // 吉林
        put("昆明", 5301);  // 云南
        put("贵阳", 5201);  // 贵州
        put("南宁", 4501);  // 广西
        put("兰州", 6201);  // 甘肃
        put("银川", 6401);  // 宁夏
        put("西宁", 6301);  // 青海
        put("乌鲁木齐", 6501); // 新疆
        put("呼和浩特", 1501); // 内蒙古
        put("拉萨", 5401);   // 西藏
    }};

    // 任务状态常量
    private static final int TASK_STATUS_PENDING = 0;
    private static final int TASK_STATUS_RUNNING = 1;
    private static final int TASK_STATUS_COMPLETED = 2;
    private static final int TASK_STATUS_FAILED = 3;

    // 数据处理状态常量
    private static final int DATA_STATUS_PENDING = 0;
    private static final int DATA_STATUS_PROCESSED = 1;
    private static final int DATA_STATUS_FAILED = 2;


    private static final AtomicInteger taskId = new AtomicInteger(1);

    // 定义要爬取的城市列表
    private static final List<String> CITIES = List.of("上海", "深圳", "杭州", "广州", "北京",  "南京", "西安", "武汉", "成都", "天津", "重庆");
    // 定义不同行业及其对应的岗位
    private static final Map<String, List<String>> INDUSTRY_JOBS = new HashMap<>() {
        {
            put("互联网", List.of("全栈工程师", "前端工程师", "后端工程师", "测试工程师", "数据分析师"));
//        put("金融", List.of("金融分析师", "投资顾问", "风险控制专员", "理财规划师"));
//        put("教育", List.of("课程研发专员", "在线教育讲师", "教学运营主管", "招生顾问"));
        }
    };

    private static final long DELAY_BETWEEN_CITIES = 5; // 不同城市爬取间隔时间（秒）
    private static final int PAGES_PER_CITY = 2; // 每个城市爬取的页数

    public static void main(String[] args) {
        // 创建一个单线程的定时任务执行器
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        // 初始延迟 0 秒，之后每隔一段时间执行一次爬取任务
        executorService.scheduleAtFixedRate(() -> {
            int currentTaskId = taskId.getAndIncrement();

            for (String city : CITIES) {
                for (Map.Entry<String, List<String>> industryEntry : INDUSTRY_JOBS.entrySet()) {
                    //String industry = industryEntry.getKey();
                    List<String> jobs = industryEntry.getValue();
                    for (String job : jobs) {
                        // 对岗位名称进行 URL 编码
                        String encodedJob = URLEncoder.encode(job, StandardCharsets.UTF_8);
                        String urlStr = "https://www.lagou.com/wn/zhaopin?fromSearch=true" +
                                "&kd=" + encodedJob +
                                "&city=" + city + "&pn=${page}";
                        // 爬取指定页数的数据
                        for (int i = 1; i <= PAGES_PER_CITY; i++) {
                            crawlPage(urlStr.replace("${page}", i + ""), currentTaskId);
                            try {
                                // 每页请求间隔 5 秒
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
                try {
                    // 不同城市爬取间隔
                    Thread.sleep(DELAY_BETWEEN_CITIES * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, 0, 10, TimeUnit.SECONDS); // 每10s执行一次完整的城市爬取任务
    }


    /**
     * 根据传入的 URL 爬取拉勾网的招聘信息
     *
     * @param urlStr 要爬取的拉勾网招聘信息页面的 URL
     * @param currentTaskId 当前任务 ID
     */
    public static void crawlPage(String urlStr, int currentTaskId) {


        StringBuilder response = new StringBuilder();
        try {
            // 创建爬虫任务记录
            int taskId = createCrawlerTask(urlStr);
            if (taskId <= 0) {
                System.err.println("创建爬虫任务失败");
                return;
            }
            // 保存原始数据到crawler_raw_data表

            String rawData = response.toString();
            int rawDataId = saveRawData(taskId, rawData);

            // ... 处理数据后更新状态 ...
            updateRawDataStatus(rawDataId, DATA_STATUS_PROCESSED);
            updateTaskStatus(taskId, TASK_STATUS_COMPLETED);

            // 连接数据库
            Connection dbConn = Dao.getConnection();
            // 定义插入职位信息的 SQL 语句
            String sql = "insert into position (title, company_id, city_id, min_experience, max_experience, education_id, min_salary, max_salary, create_by) values (?,?,?,?,?,?,?,?,?)";
            // 创建 PreparedStatement 对象，用于执行 SQL 语句并返回生成的主键
            PreparedStatement ps = dbConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // 定义插入技能信息的 SQL 语句
            String sql2 = "insert into skill (name, category,create_by,create_time) values (?,?,?,CURRENT_TIMESTAMP)";
            // 创建 PreparedStatement 对象，用于执行 SQL 语句并返回生成的主键
            PreparedStatement ps2 = dbConn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            // 定义插入职位-技能关联信息的 SQL 语句
            //String sql3 = "insert into position_skill (position_id, skill_id, create_by) values (?,?,?)";
            // 创建 PreparedStatement 对象，用于执行 SQL 语句
            //PreparedStatement ps3 = dbConn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);



            // 根据传入的 URL 字符串创建 URL 对象
            URL url = new URL(urlStr);
            // 打开 URL 连接并将其转换为 HttpURLConnection 对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为 GET
            connection.setRequestMethod("GET");
            // 可选：添加请求头
            connection.setRequestProperty("User-Agent", "Java HttpURLConnection Example");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setRequestProperty("Cookie", "index_location_city=%E6%88%90%E9%83%BD; RECOMMEND_TIP=1; _ga=GA1.2.2134300414.1742868852; user_trace_token=20250325101416-ec46d1b3-ad94-481a-a8a9-5027f681bb5e; LGUID=20250325101416-a613f76c-b068-494b-ac9f-0a145e7bda8a; gate_login_token=v1####8c33dbc8c784958568e7df89bb3a1f1aaf84b522f17eecee; LG_HAS_LOGIN=1; showExpriedIndex=1; showExpriedCompanyHome=1; showExpriedMyPublish=1; hasDeliver=27; privacyPolicyPopup=false; LG_LOGIN_USER_ID=v1####11571776d6e0c759174253a85c382a8e70be24e0b90f3ed3; __lg_stoken__=2ee0c6ef6126cc4272c143f2bd68180be9a38c91a056e0ec5518e16c39b2062c1300596d30f44241667661a8d2077c31e60bc5d56153d2692385e33308b4823d09fe8db3187d; Hm_lvt_4233e74dff0ae5bd0a3d81c6ccf756e6=1742868854,1742969904; _gid=GA1.2.369963393.1742969904; index_location_city=%E6%88%90%E9%83%BD; _ga_DDLTLJDLHH=GS1.2.1742969904.1.1.1742970135.60.0.0; __RESUME_COMPLETE_POPOVER__=1; _putrc=A8512D349FF028F7; JSESSIONID=ABAABJAAABHABBIED2D7394D9F3516DD1DBD6EFBD3BDDD6; login=true; unick=%E5%90%B4%E5%85%88%E7%94%9F; WEBTJ-ID=20250326145925-195d13faa64ef3-03f8d640ff9c3a-26011d51-2073600-195d13faa65116f; sensorsdata2015session=%7B%7D; X_HTTP_TOKEN=65f0411f77686a8950837924712ae4d76565c0e917; X_MIDDLE_TOKEN=6f92983680a54644203e7c4a0432f81d; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%226889057%22%2C%22first_id%22%3A%22195cb13d8132ba-0189e69f84e7b3-26011d51-2073600-195cb13d81413fb%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24os%22%3A%22Windows%22%2C%22%24browser%22%3A%22Chrome%22%2C%22%24browser_version%22%3A%22134.0.0.0%22%7D%2C%22%24device_id%22%3A%22195cb13d8132ba-0189e69f84e7b3-26011d51-2073600-195cb13d81413fb%22%7D");

            // 获取响应码
            int responseCode = connection.getResponseCode();

            System.out.println("GET Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String html = response.toString();
                int start = html.indexOf("<script id=\"__NEXT_DATA__\" type=\"application/json\">") +
                        "<script id=\"__NEXT_DATA__\" type=\"application/json\">".length();
                int end = html.indexOf("<script nomodule=");
                // 检查索引是否有效
                if (start >= 0 && end >= 0 && start < end) {
                    try {
                        html = html.substring(start, end).replace("</script>", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateCrawlerTaskStatus(currentTaskId, 3);
                        return;
                    }
                } else {
                    System.out.println("未找到有效的 JSON 数据区域");
                    updateCrawlerTaskStatus(currentTaskId, 3);
                    return;
                }

                JSONObject jsonObject = JSONUtil.parseObj(html);
                JSONObject positionResult = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject) ((JSONObject) jsonObject.get("props")).get("pageProps")).get("initData")).get("content")).get("positionResult");
                if (positionResult == null) {
                    System.out.println("爬取失败，未找到职位信息");
                    updateCrawlerTaskStatus(currentTaskId, 3);
                }
                JSONArray jsonArray = (JSONArray) positionResult.get("result");
                if (jsonArray != null) {
                    // 使用增强 for 循环提高代码可读性
                    for (Object obj : jsonArray) {
                        if (obj instanceof JSONObject) {
                            JSONObject position = (JSONObject) obj;
                            // 添加空值检查，避免打印 null
                            String positionName = position.getStr("positionName");
                            String companyShortName = position.getStr("companyShortName");
                            String cityName = position.getStr("city");
                            String workYear = position.getStr("workYear");
                            JSONArray skillLables = position.getJSONArray("skillLables");
                            String education = position.getStr("education");
                            String salary = position.getStr("salary");

                            // 检查关键字段是否为空，若任一为空则跳过本次插入
                            if (positionName == null || positionName.isEmpty() ||
                                    companyShortName == null || companyShortName.isEmpty() ||
                                    cityName == null || cityName.isEmpty() ||
                                    workYear == null || workYear.isEmpty() ||
                                    education == null || education.isEmpty() ||
                                    salary == null || salary.isEmpty()) {
                                //System.out.println("部分关键字段为空，跳过该条记录");
                                continue;
                            }

                            System.out.println("职位名称: " + positionName);
                            System.out.println("公司简称: " + companyShortName);
                            System.out.println("城市: " + cityName);
                            System.out.println("经验要求: " + workYear);
                            System.out.println("技能标签: " + skillLables);
                            System.out.println("学历要求: " + education);
                            System.out.println("薪资范围: " + salary);
                            System.out.println("-------------------------------");

                            try {
                                // 获取公司 ID
                                int companyId = getCompanyId(dbConn, companyShortName);
                                if (companyId == -1) {
                                    continue;
                                }
                                // 获取城市 ID
                                int cityId = getCityIdFromDict(dbConn, cityName);
                                if (cityId == -1) {
                                    continue;
                                }

                                // 获取学历 ID
                                int educationId = getEducationIdFromDict(dbConn, education);
                                if (educationId == -1) {
                                    continue;
                                }
                                // 解析经验要求
                                int[] experience = parseExperience(workYear);
                                if (experience == null) {
                                    System.out.println("经验要求解析失败，跳过该条记录: " + workYear);
                                    continue;
                                }
                                int minExperience = experience[0];
                                int maxExperience = experience[1];
                                // 解析薪资范围
                                BigDecimal[] salaryRange = parseSalary(salary);
                                BigDecimal minSalary = salaryRange[0];
                                BigDecimal maxSalary = salaryRange[1];


                                ps.setString(1, positionName);
                                ps.setInt(2, companyId);
                                ps.setInt(3, cityId);
                                ps.setInt(4, minExperience);
                                ps.setInt(5, maxExperience);
                                ps.setInt(6, educationId);
                                ps.setBigDecimal(7, minSalary);
                                ps.setBigDecimal(8, maxSalary);
                                ps.setString(9, "LaGou");
                                ps.executeUpdate();
                                ResultSet rs = ps.getGeneratedKeys();
                                rs.next();
                                // 在职位插入后添加技能关联
                                int positionId = rs.getInt(1);
                                // 修改技能插入逻辑
                                JSONArray skills = (JSONArray) position.get("skillLables");
                                if (skills != null) {
                                    for (int j = 0; j < skills.size(); j++) {
                                        String skillName = skills.getStr(j);
                                        if (skillName != null && !skillName.isEmpty()) {
                                            // 先检查技能是否已存在
                                            String sql4 = "SELECT id FROM skill WHERE name = ?";
                                            try (PreparedStatement ps4 = dbConn.prepareStatement(sql4)) {
                                                ps4.setString(1, skillName);
                                                ResultSet rs4 = ps4.executeQuery();
                                                if (!rs4.next()) {
                                                    // 技能不存在，插入新技能
                                                    ps2.setString(1, skillName);
                                                    ps2.setString(2, "技术");
                                                    ps2.setString(3, "LaGou");
                                                    ps2.executeUpdate();
                                                }
                                            }

                                            // 获取技能ID
                                            int skillId = -1;
                                            try (PreparedStatement ps4 = dbConn.prepareStatement(sql4)) {
                                                ps4.setString(1, skillName);
                                                ResultSet rs4 = ps4.executeQuery();
                                                if (rs4.next()) {
                                                    skillId = rs4.getInt(1);
                                                }
                                            }

                                            if (skillId != -1) {
                                                // 插入position_skill关联
                                                String sql3 = "INSERT INTO position_skill (position_id, skill_id, create_by) VALUES (?, ?, ?)";
                                                try (PreparedStatement ps3 = dbConn.prepareStatement(sql3)) {
                                                    ps3.setInt(1, positionId);
                                                    ps3.setInt(2, skillId);
                                                    ps3.setString(3, "LaGou");
                                                    ps3.executeUpdate();
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // 替换为日志记录
                                Logger.getLogger(LaGouCrawler.class.getName()).log(Level.SEVERE, "数据库插入出错", e);
                            }
                        }
                    }
                }
                updateCrawlerTaskStatus(currentTaskId, 2);
            } else {
                System.out.println("请求失败，响应码: " + responseCode);
                updateCrawlerTaskStatus(currentTaskId, 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateCrawlerTaskStatus(currentTaskId, 3);
        }

    }

    //    private static String cleanTechStack(String techStack) {
//        return techStack.replaceAll("[\\[\\]\"]", "");
//    }
// 创建爬虫任务
    private static int createCrawlerTask(String url) {
        try (Connection conn = Dao.getConnection()) {
            String sql = "INSERT INTO crawler_task (source, url, status,create_by) VALUES (?, ?, ?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "拉勾网");
            ps.setString(2, url);
            ps.setInt(3, TASK_STATUS_RUNNING);
            ps.setString(4, "LaGou");
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 保存原始数据
    private static int saveRawData(int taskId, String rawData) {
        try (Connection conn = Dao.getConnection()) {
            String sql = "INSERT INTO crawler_raw_data (task_id, raw_data, status) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, taskId);
            ps.setString(2, rawData);
            ps.setInt(3, DATA_STATUS_PENDING);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 更新任务状态
    private static void updateTaskStatus(int taskId, int status) {
        try (Connection conn = Dao.getConnection()) {
            String sql = "UPDATE crawler_task SET status = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 更新原始数据状态
    private static void updateRawDataStatus(int rawDataId, int status) {
        try (Connection conn = Dao.getConnection()) {
            String sql = "UPDATE crawler_raw_data SET status = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, rawDataId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getCompanyId(Connection dbConn, String companyName) {
        try {
            String sql = "SELECT id FROM company WHERE name = ?";
            PreparedStatement ps = dbConn.prepareStatement(sql);
            ps.setString(1, companyName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            // 若公司不存在，插入新记录
            sql = "INSERT INTO company (name) VALUES (?)";
            ps = dbConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, companyName);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            Logger.getLogger(LaGouCrawler.class.getName()).log(Level.SEVERE, "获取公司 ID 出错", e);
        }
        return -1;
    }


    /**
     * 更新爬虫任务状态
     *
     * @param taskId 任务ID
     * @param status 任务状态
     */
    private static void updateCrawlerTaskStatus(int taskId, int status) {
        try {
            Connection dbConn = Dao.getConnection();
            String sql = "UPDATE crawler_task SET status = ? WHERE id = ?";
            PreparedStatement ps = dbConn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, taskId);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.getLogger(LaGouCrawler.class.getName()).log(Level.SEVERE, "更新爬虫任务状态出错", e);
        }
    }

    /**
     * 解析经验要求
     *
     * @param workYear 经验要求字符串
     * @return 包含最小和最大经验值的数组
     */
    private static int[] parseExperience(String workYear) {
        // 默认返回null表示解析失败
        if (workYear == null || workYear.isEmpty()) {
            return null;
        }

        try {
            if ("经验不限".equals(workYear)) {
                return new int[]{0, 0};
            } else if ("经验在校".equals(workYear)) {
                return new int[]{0, 1};
            } else {
                String[] parts = workYear.replace("经验", "").replace("年", "").split("-");
                int min = Integer.parseInt(parts[0]);
                int max = parts.length > 1 ? Integer.parseInt(parts[1]) : min;
                return new int[]{min, max};
            }
        } catch (Exception e) {
            Logger.getLogger(LaGouCrawler.class.getName()).log(Level.WARNING, "解析经验要求出错: " + workYear, e);
            return null;
        }
    }

    /**
     * 解析薪资范围
     *
     * @param salary 薪资范围字符串
     * @return 包含最小和最大薪资的 BigDecimal 数组
     */
    private static BigDecimal[] parseSalary(String salary) {
        BigDecimal minSalary = BigDecimal.ZERO;
        BigDecimal maxSalary = BigDecimal.ZERO;

        try {
            String[] parts = salary.replace("k", "").split("-");
            //String[] parts = salary.split("-");
            minSalary = new BigDecimal(parts[0]);
            maxSalary = new BigDecimal(parts[1]);
        } catch (Exception e) {
            Logger.getLogger(LaGouCrawler.class.getName()).log(Level.WARNING, "解析薪资范围出错: " + salary, e);
        }

        return new BigDecimal[]{minSalary, maxSalary};
    }

    /**
     * 获取城市 ID
     *
     * @param dbConn 数据库连接
     * @param cityName 城市名称
     * @return 城市 ID，若不存在则插入新记录并返回新 ID，出错返回 -1
     */
    private static int getCityIdFromDict(Connection dbConn, String cityName) {

        try {
            // 首先检查城市是否已存在
            String sql = "SELECT id FROM dict_city WHERE name = ?";
            PreparedStatement ps = dbConn.prepareStatement(sql);
            ps.setString(1, cityName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            // 若城市不存在，插入新记录
            Integer cityCode = CITY_CODES.get(cityName);
            String code = (cityCode != null) ? String.valueOf(cityCode) : "未知代码";

            String sql1 = "INSERT INTO dict_city (name, code, create_by) VALUES (?, ?, ?)";
            PreparedStatement ps1 = dbConn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, cityName);
            ps1.setString(2, code);
            ps1.setString(3, "LaGouCrawler");
            ps1.executeUpdate();

            rs = ps1.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            Logger.getLogger(LaGouCrawler.class.getName()).log(Level.SEVERE, "获取城市 ID 出错", e);
        }
        return -1;
    }

    private static int getEducationIdFromDict(Connection dbConn, String educationName) {
        try {
            String sql = "SELECT id FROM dict_education WHERE name = ?";
            PreparedStatement ps = dbConn.prepareStatement(sql);
            ps.setString(1, educationName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            // 若学历不存在，插入新记录
            String sql1 = "INSERT INTO dict_education (name, level, create_by) VALUES (?, ?, ?)";
            //ps = dbConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement ps1 = dbConn.prepareStatement(sql1,Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, educationName);
            ps1.setInt(2, getEducationLevel(educationName)); // 根据学历名称获取等级
            ps1.setString(3, "LaGouCrawler");
            ps1.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            Logger.getLogger(LaGouCrawler.class.getName()).log(Level.SEVERE, "获取学历 ID 出错", e);
        }
        return -1;
    }

    private static int getEducationLevel(String educationName) {
        switch (educationName) {
            case "博士":
                return 5;
            case "硕士":
                return 4;
            case "本科":
                return 3;
            case "大专":
                return 2;
            case "高中及以下":
                return 1;
            default:
                return 0;
        }
    }
}

//%25E5%2585%25A8%25E6%25A0%2588%25E5%25B7%25A5%25E7%25A8%258B%25E5%25B8%2588
//String urlStr = "https://www.lagou.com/wn/zhaopin?fromSearch=true" +
//                        "&kd=%25E5%2585%25A8%25E6%25A0%2588%25E5%25B7%25A5%25E7%25A8%258B%25E5%25B8%2588" +
//                        "&city=" + city + "&pn=${page}";
//                        "&city=" + city + "&pn=${page}";
//                        "&city=" + city + "&pn=${page}";
//                        "&city=" + city + "&pn=${page}";