<template>
  <div class="chart" ref="educationSalaryRef"></div>
</template>

<script>
import * as echarts from 'echarts'
import axios from "axios";
export default {
  name: 'EducationSalary',
  data() {
    return {
      salaryChart: null,
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
      this.getEducationAndSalary().then(data => {
        this.initChart(data)
      })
    })
  },
  beforeUnmount() {
    if (this.salaryChart) {
      this.salaryChart.dispose()
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
      this.getEducationAndSalary().then(data => {
        if (data && data.length > 0) {
          if (this.salaryChart) {
            this.updateChart(data);
          } else {
            this.initChart(data);
          }
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
    
    // 更新图表数据
    updateChart(data) {
      //颜色映射
      const color = ['#3eabff','#20cc98', '#33b565', '#205bcf'];
      let count = 0;
      //数据源
      const dataArr = data.map(item => {
        count++;
        return {
          value: Math.floor(item.avg_salary),
          name: item.education_level,
          itemStyle: {
            color: color[count - 1]
          }
        }
      });
      
      // 更新图表配置
      this.salaryChart.setOption({
        series: [
          {
            data: dataArr
          }
        ]
      });
    },
    initChart(data) {
      // 初始化图表
      this.salaryChart = echarts.init(this.$refs.educationSalaryRef)
      //颜色映射
      const color = ['#3eabff','#20cc98', '#33b565', '#205bcf'];
      let count = 0;
      //数据源
      const dataArr = data.map(item => {
        count++;
        return {
          value: Math.floor(item.avg_salary),
          name: item.education_level,
          itemStyle: {
            color: color[count - 1]
          }
        }
      })
      // 配置图表选项
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
        legend: {
          top: 'bottom',
          textStyle: {
            color: '#fff',
            textShadow: '0 0 5px rgba(62, 171, 255, 0.5)'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}k',
          backgroundColor: 'rgba(10, 20, 60, 0.8)',
          borderColor: '#3eabff',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          extraCssText: 'box-shadow: 0 0 10px rgba(62, 171, 255, 0.5);'
        },
        series: [
          {
            name: '学历平均薪资',
            type: 'pie',
            radius: ['30%', '70%'],
            center: ['50%', '50%'],
            roseType: 'area',
            itemStyle: {
              borderRadius: 8,
              borderColor: '#062d96',
              borderWidth: 2,
              shadowBlur: 10,
              shadowColor: 'rgba(62, 171, 255, 0.5)'
            },
            label: {
              color: '#fff',
              textShadow: '0 0 5px rgba(0, 0, 0, 0.5)'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 20,
                shadowColor: 'rgba(62, 171, 255, 0.8)'
              },
              label: {
                textShadow: '0 0 8px rgba(0, 0, 0, 0.8)'
              }
            },
            data: dataArr
          }
        ]
      }
      
      // 应用配置
      this.salaryChart.setOption(option)
      
      // 窗口大小变化时自动调整图表大小
      this.resizeHandler = () => {
        this.salaryChart.resize()
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    // 获取学历的平均薪资数据
    async getEducationAndSalary () {
      const response = await axios.get('http://localhost:9090/loadChart/getEducationLevelAndAvgSalary')
      if (response.data.code === 1) {
        return response.data.data
      }
      return [];
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