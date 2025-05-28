<template>
  <div class="chart" ref="cityJobHeatmapRef"></div>
</template>

<script>
import * as echarts from 'echarts'
import axios from "axios";

export default {
  name: 'CityJobHeatmap',
  data() {
    return {
      heatmapChart: null,
      resizeHandler: null,
      timerId: null  // 添加定时器ID
    }
  },
  created() {
    // 获取数据
    this.getData();
    // 定时更新数据
    this.refresh();
  },
  mounted() {
    this.$nextTick(() => {
      this.initHeatmap()
    })
  },
  beforeUnmount() {
    if (this.heatmapChart) {
      this.heatmapChart.dispose()
    }
    if (this.resizeHandler) {
      window.removeEventListener('resize', this.resizeHandler)
    }
    // 清除定时器
    if (this.timerId) {
      clearTimeout(this.timerId);
    }
  },
  methods: {
    // 获取数据的方法
    getData() {
      // 获取城市和职位数据并更新图表
      this.getCityAndSalary().then(responseData => {
        if (this.heatmapChart && responseData && responseData.length > 0) {
          this.updateHeatmapData(responseData);
        }
      });
    },
    
    // 用于定期刷新
    refresh() {
      this.timerId = setTimeout(() => {
        this.getData();
        this.refresh();
      }, 5 * 1000); // 5秒刷新一次
    },
    
    // 更新热力图数据
    updateHeatmapData(responseData) {
      if (!responseData || responseData.length === 0) return;
      
      // 定义城市列表
      const capitalCities = [
        '北京', '上海', 
        '杭州', '深圳','济南',
        '广州',
        '成都',
      ];
      
      // 定义职位类型及其关键词
      const jobTypes = [
        { name: '全栈工程师', keywords: ['全栈'] },
        { name: '前端工程师', keywords: ['前端','web'] },
        { name: '后端工程师', keywords: ['后端','java','JAVA','Java','Python','python','Go','go','Golang','react'] },
        { name: '测试工程师', keywords: ['测试'] }
      ];
      
      let cities = [];
      let jobs = [];
      let data = [];
      let cityMap = new Map();
      
      // 过滤数据
      const filteredData = responseData.filter(cityData => 
        capitalCities.includes(cityData.cityName)
      );
      
      // 提取唯一的城市
      filteredData.forEach(cityData => {
        const cityName = cityData.cityName;
        if (!cityMap.has(cityName)) {
          cityMap.set(cityName, cities.length);
          cities.push(cityName);
        }
      });
      
      // 使用职位类型作为纵轴
      jobs = jobTypes.map(type => type.name);
      
      // 初始化数据
      const dataMatrix = Array(cities.length).fill().map(() => Array(jobs.length).fill(0));
      
      // 处理每个城市的职位列表
      filteredData.forEach(cityData => {
        const cityName = cityData.cityName;
        const cityIndex = cityMap.get(cityName);
        
        // 处理每个城市的职位列表
        cityData.jobList.forEach(jobData => {
          const jobTitle = jobData.jobTitle.toLowerCase();
          const jobCount = parseInt(jobData.jobOpenings);
          
          // 模糊匹配职位类型
          for (let j = 0; j < jobTypes.length; j++) {
            // 检查职位标题是否包含任一关键词
            if (jobTypes[j].keywords.some(keyword => jobTitle.includes(keyword.toLowerCase()))) {
              // 累加职位需求数量
              dataMatrix[cityIndex][j] += jobCount;
              break;
            }
          }
        });
      });
      
      // 将矩阵转换为热力图数据格式
      for (let i = 0; i < cities.length; i++) {
        for (let j = 0; j < jobs.length; j++) {
          if (dataMatrix[i][j] > 0) {
            data.push([i, j, dataMatrix[i][j]]);
          }
        }
      }
      
      // 计算最大需求量，用于visualMap设置
      const maxValue = data.length > 0 ? Math.max(...data.map(item => item[2])) : 100;
      
      // 更新图表数据
      this.heatmapChart.setOption({
        xAxis: {
          data: cities
        },
        yAxis: {
          data: jobs
        },
        visualMap: {
          max: maxValue || 100
        },
        series: [
          {
            data: data
          }
        ]
      });
    },
    initHeatmap() {
      // 初始化热力图
      this.heatmapChart = echarts.init(this.$refs.cityJobHeatmapRef)
      
      // 从后端获取的数据处理
      let cities = [];
      let jobs = [];
      let data = [];
      let cityMap = new Map();
      let jobMap = new Map();
      
      // 获取城市和职位数据
      this.getCityAndSalary().then(responseData => {
        if (!responseData || responseData.length === 0) return;
        
        // 定义城市列表
        const capitalCities = [
          '北京', '上海', 
          '杭州', '深圳','济南',
          '广州',
          '成都',
        ];
        
        // 定义职位类型及其关键词
        const jobTypes = [
          { name: '全栈工程师', keywords: ['全栈'] },
          { name: '前端工程师', keywords: ['前端','web'] },
          { name: '后端工程师', keywords: ['后端','java','JAVA','Java','Python','python','Go','go','Golang','react'] },
          { name: '测试工程师', keywords: ['测试'] }
        ];
        
        // 过滤数据
        const filteredData = responseData.filter(cityData => 
          capitalCities.includes(cityData.cityName)
        );
        
        // 提取唯一的城市
        filteredData.forEach(cityData => {
          const cityName = cityData.cityName;
          if (!cityMap.has(cityName)) {
            cityMap.set(cityName, cities.length);
            cities.push(cityName);
          }
        });
        
        // 使用职位类型作为纵轴
        jobs = jobTypes.map(type => type.name);
        
        // 初始化数据
        const dataMatrix = Array(cities.length).fill().map(() => Array(jobs.length).fill(0));
        
        // 处理每个城市的职位列表
        filteredData.forEach(cityData => {
          const cityName = cityData.cityName;
          const cityIndex = cityMap.get(cityName);
          
          // 处理每个城市的职位列表
          cityData.jobList.forEach(jobData => {
            const jobTitle = jobData.jobTitle.toLowerCase();
            const jobCount = parseInt(jobData.jobOpenings);
            
            // 模糊匹配职位类型
            for (let j = 0; j < jobTypes.length; j++) {
              // 检查职位标题是否包含任一关键词
              if (jobTypes[j].keywords.some(keyword => jobTitle.includes(keyword.toLowerCase()))) {
                // 累加职位需求数量
                dataMatrix[cityIndex][j] += jobCount;
                break;
              }
            }
          });
        });
        
        // 将矩阵转换为热力图数据格式
        for (let i = 0; i < cities.length; i++) {
          for (let j = 0; j < jobs.length; j++) {
            if (dataMatrix[i][j] > 0) {
              data.push([i, j, dataMatrix[i][j]]);
            }
          }
        }
        
        // 计算最大需求量，用于visualMap设置
        const maxValue = data.length > 0 ? Math.max(...data.map(item => item[2])) : 100;
        
        // 配置热力图选项
        var option = {
          title: {
            text: '',
            left: 'center',
            top: 0,
            textStyle: {
              color: '#fff',
              fontSize: 16,
              textShadow: '0 0 10px rgba(62, 171, 255, 0.7)'
            }
          },
          tooltip: {
            position: 'top',
            formatter: function(params) {
              return '城市: ' + cities[params.value[0]] + '<br>' +
                     '职位: ' + jobs[params.value[1]] + '<br>' +
                     '需求数量: ' + params.value[2]
            },
            backgroundColor: 'rgba(10, 20, 60, 0.8)',
            borderColor: '#3eabff',
            borderWidth: 1,
            textStyle: {
              color: '#fff'
            },
            extraCssText: 'box-shadow: 0 0 10px rgba(62, 171, 255, 0.5);'
          },
          grid: {
            height: '65%',
            top: '8%',
            left: '18%',
            right: '5%'
          },
          xAxis: {
            type: 'category',
            data: cities,
            splitArea: {
              show: true
            },
            axisLabel: {
              color: '#fff',
              interval: 0,
              fontSize: 10,
              rotate: 30,
              textShadow: '0 0 5px rgba(62, 171, 255, 0.5)'
            },
          },
          yAxis: {
            type: 'category',
            data: jobs,
            splitArea: {
              show: true
            },
            axisLabel: {
              color: '#fff',
              width: 100,
              overflow: 'break',
              fontSize: 9,
              textShadow: '0 0 5px rgba(62, 171, 255, 0.5)',
              formatter: function(value) {
                if (value.length > 15) {
                  return value.substring(0, 15) + '...'
                }
                return value
              }
            }
          },
          visualMap: {
            min: 0,
            max: maxValue || 100,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '2%',
            itemWidth: 10,
            itemHeight: 60,
            textStyle: {
              color: '#fff',
              fontSize: 10,
              textShadow: '0 0 5px rgba(62, 171, 255, 0.5)'
            },
            inRange: {
              color: ['#3eabff', '#205bcf']
            }
          },
          series: [
            {
              name: '职位需求',
              type: 'heatmap',
              data: data,
              label: {
                show: false,
                color: '#fff'
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 15,
                  shadowColor: 'rgba(62, 171, 255, 0.8)'
                }
              }
            }
          ]
        };

        this.heatmapChart.setOption(option);
      });
      
      // 窗口大小变化时自动调整图表大小
      this.resizeHandler = () => {
        this.heatmapChart.resize()
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    async getCityAndSalary() {
      // 获取城市和职位需求数据
      try {
        const res = await axios.get('http://localhost:9090/loadChart/getCityAndJobTitleAndJobOpenings');
        if (res.data.code === 1) {
          return res.data.data;
        }
        return [];
      } catch (error) {
        console.error('获取城市职位数据失败:', error);
        return [];
      }
    }
  }
}
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
  min-height: 200px;
  position: relative;
  border-radius: 5px;
  overflow: hidden;
}

/* 添加发光边框效果 */
.chart::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 1px solid rgba(62, 171, 255, 0.3);
  border-radius: 5px;
  box-shadow: 0 0 15px rgba(62, 171, 255, 0.3), inset 0 0 15px rgba(62, 171, 255, 0.2);
  pointer-events: none;
  z-index: 1;
}

/* 添加扫描线动画效果 */
.chart::after {
  content: '';
  position: absolute;
  top: -150%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to bottom,
    rgba(62, 171, 255, 0) 0%,
    rgba(62, 171, 255, 0.1) 50%,
    rgba(62, 171, 255, 0) 100%
  );
  transform: rotate(30deg);
  animation: scanLine 8s linear infinite;
  pointer-events: none;
  z-index: 2;
}

@keyframes scanLine {
  0% {
    top: -150%;
  }
  100% {
    top: 150%;
  }
}
</style>