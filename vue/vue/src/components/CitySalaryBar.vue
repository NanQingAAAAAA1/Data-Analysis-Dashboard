<template>
  <div class="chart" ref="citySalaryBarRef"></div>
</template>

<script>
import * as echarts from 'echarts'
import axios from "axios";

export default {
  name: 'CitySalaryBar',
  data() {
    return {
      barChart: null,
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
      this.getCityAndSalary().then((data) =>{
        this.initBarChart(data)
      })
    })
  },
  beforeUnmount() {
    if (this.barChart) {
      this.barChart.dispose()
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
      this.getCityAndSalary().then(data => {
        if (this.barChart && data && data.length > 0) {
          this.updateBarChart(data);
        } else if (!this.barChart && data && data.length > 0) {
          this.initBarChart(data);
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
    
    // 更新柱状图数据
    updateBarChart(data) {
      // 城市和薪资数据
      const cities = data.map(item => item.city_name);
      const salaries = data.map(item => Math.floor(item.salary_percentage));
      
      // 更新柱状图配置
      this.barChart.setOption({
        xAxis: {
          data: cities
        },
        series: [
          {
            data: salaries
          }
        ]
      });
    },
    
    initBarChart(data) {
      // 初始化柱状图
      this.barChart = echarts.init(this.$refs.citySalaryBarRef)
      
      // 城市和薪资数据
      const cities =  data.map(item => item.city_name);
      const salaries = data.map(item => Math.floor(item.salary_percentage));
      
      // 配置柱状图选项
      const option = {
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
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: '{b}: {c}k',
          backgroundColor: 'rgba(10, 20, 60, 0.8)',
          borderColor: '#3eabff',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          extraCssText: 'box-shadow: 0 0 10px rgba(62, 171, 255, 0.5);'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '25%',  
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: cities,
          axisLabel: {
            color: '#fff',
            interval: 0,
            rotate: 45,
            fontSize: 10, 
            margin: 8,  
            textShadow: '0 0 5px rgba(62, 171, 255, 0.5)' 
          },
          axisLine: {
            lineStyle: {
              color: '#fff'
            }
          }
        },
        yAxis: {
          type: 'value',
          name: '薪资（单位/k）',
          nameTextStyle: {
            color: '#fff',
            fontSize: 10, 
            textShadow: '0 0 5px rgba(62, 171, 255, 0.5)' 
          },
          axisLabel: {
            color: '#fff',
            fontSize: 10,
            textShadow: '0 0 5px rgba(62, 171, 255, 0.5)' 
          },
          axisLine: {
            lineStyle: {
              color: '#fff'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.1)'
            }
          }
        },
        series: [
          {
            data: salaries,
            type: 'bar',
            barWidth: '50%',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#3eabff' },
                { offset: 1, color: '#205bcf' }
              ]),
              shadowColor: 'rgba(62, 171, 255, 0.5)',
              shadowBlur: 10 
            },
            label: {
              show: true,
              position: 'top',
              color: '#fff',
              fontSize: 9,
              textShadow: '0 0 5px rgba(62, 171, 255, 0.5)'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 15,
                shadowColor: 'rgba(62, 171, 255, 0.8)'
              }
            }
          }
        ]
      }
      
      // 应用配置
      this.barChart.setOption(option)
      
      // 窗口大小变化时自动调整图表大小
      this.resizeHandler = () => {
        this.barChart.resize()
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    async getCityAndSalary () {
      return  await axios.get('http://localhost:9090/loadChart/getTop10CitySalary')
      .then(res => {
        return res.data.data;
      })
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