<template>
  <div class="chart" ref="cityLinkedSalaryRef"></div>
</template>

<script>
import * as echarts from 'echarts'
import axios from "axios";

export default {
  name: 'CityLinkedSalary',
  data() {
    return {
      radarChart: null,
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
      this.getCityExperienceData().then(data => {
        this.initRadarChart(data)
      })
    })
  },
  beforeUnmount() {
    if (this.radarChart) {
      this.radarChart.dispose()
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
      this.getCityExperienceData().then(data => {
        if (this.radarChart && data && data.length > 0) {
          this.updateRadarChart(data);
        } else if (!this.radarChart && data && data.length > 0) {
          this.initRadarChart(data);
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
    
    // 更新雷达图数据
    updateRadarChart(data) {
      // 定义样式颜色
      const itemstyle = ['#3eabff', '#20cc98', '#205bcf'];
      const rgb = ['rgba(62, 171, 255, 0.4)', 'rgba(32, 204, 152, 0.4)', 'rgba(32, 91, 207, 0.4)'];
      
      // 获取城市列表和最大值
      const cities = [...new Set(data.map(item => item.cityName))];
      const maxValue = data[0].maxPositionCount;
      
      // 按经验年限分组数据
      const experienceGroups = {};
      data.forEach(item => {
        if (!experienceGroups[item.experienceLevel]) {
          experienceGroups[item.experienceLevel] = {};
        }
        experienceGroups[item.experienceLevel][item.cityName] = item.positionCount;
      });
      
      // 构建雷达图数据
      const seriesData = [];
      const experienceLevels = [1, 3, 5];
      
      experienceLevels.forEach((level, index) => {
        const values = cities.map(city => experienceGroups[level][city] || 0);
        
        seriesData.push({
          value: values,
          name: level + '年经验',
          itemStyle: {
            color: itemstyle[index]
          },
          lineStyle: {
            color: itemstyle[index]
          },
          areaStyle: {
            color: rgb[index]
          }
        });
      });
      
      // 构建雷达图指示器
      const indicators = cities.map(city => {
        return { name: city, max: maxValue };
      });
      
      // 更新雷达图配置
      this.radarChart.setOption({
        radar: {
          indicator: indicators
        },
        series: [
          {
            data: seriesData
          }
        ]
      });
    },
    
    initRadarChart(data) {
      // 初始化雷达图
      this.radarChart = echarts.init(this.$refs.cityLinkedSalaryRef)
      
      // 定义样式颜色
      const itemstyle = ['#3eabff', '#20cc98', '#205bcf'];
      const rgb = ['rgba(62, 171, 255, 0.4)', 'rgba(32, 204, 152, 0.4)', 'rgba(32, 91, 207, 0.4)'];
      
      // 获取城市列表和最大值
      const cities = [...new Set(data.map(item => item.cityName))];
      const maxValue = data[0].maxPositionCount;
      
      // 按经验年限分组数据
      const experienceGroups = {};
      data.forEach(item => {
        if (!experienceGroups[item.experienceLevel]) {
          experienceGroups[item.experienceLevel] = {};
        }
        experienceGroups[item.experienceLevel][item.cityName] = item.positionCount;
      });
      
      // 构建雷达图数据
      const seriesData = [];
      const experienceLevels = [1, 3, 5];
      
      experienceLevels.forEach((level, index) => {
        const values = cities.map(city => experienceGroups[level][city] || 0);
        
        seriesData.push({
          value: values,
          name: level + '年经验',
          itemStyle: {
            color: itemstyle[index]
          },
          lineStyle: {
            color: itemstyle[index]
          },
          areaStyle: {
            color: rgb[index]
          }
        });
      });
      
      // 构建雷达图指示器
      const indicators = cities.map(city => {
        return { name: city, max: maxValue };
      });
      
      // 配置雷达图选项
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          textStyle: {
            color: '#fff'
          },
          selectedMode: false,
          data: experienceLevels.map((level, index) => {
            return {
              name: level + '年经验',
              itemStyle: {
                color: itemstyle[index]
              }
            };
          })
        },
        radar: {
          indicator: indicators,
          center: ['40%', '55%'],
          radius: '65%', 
          splitArea: {
            areaStyle: {
              color: ['rgba(0, 35, 120, 0.1)', 'rgba(0, 35, 120, 0.2)']
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.3)'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.3)'
            }
          },
          name: {
            textStyle: {
              color: '#fff'
            }
          }
        },
        series: [
          {
            name: '职位数量',
            type: 'radar',
            data: seriesData
          }
        ]
      };

      // 应用配置
      this.radarChart.setOption(option);

      // 窗口大小变化时自动调整图表大小
      this.resizeHandler = () => {
        this.radarChart.resize();
      };
      window.addEventListener('resize', this.resizeHandler);
    },
    
    async getCityExperienceData() {
      try {
        const response = await axios.get('http://localhost:9090/loadChart/getNorthSouthGuangdongShenzhenAndExperience');
        if (response.data.code === 1) {
          return response.data.data;
        }
        return [];
      } catch (error) {
        console.error('获取城市经验数据失败:', error);
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