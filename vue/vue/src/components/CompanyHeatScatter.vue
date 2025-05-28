<template>
  <div class="chart" ref="companyHeatScatterRef"></div>
</template>

<script>
import * as echarts from 'echarts'
import axios from 'axios'

export default {
  name: 'CompanyHeatScatter',
  data() {
    return {
      scatterChart: null,
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
      this.initScatter()
    })
  },
  beforeUnmount() {
    if (this.scatterChart) {
      this.scatterChart.dispose()
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
      this.getCompanyData().then(companyData => {
        if (this.scatterChart && companyData && companyData.length > 0) {
          this.updateScatterData(companyData);
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
    
    // 更新散点图数据
    updateScatterData(companyData) {
      if (!companyData || companyData.length === 0) return;
      
      // 处理数据为散点图格式
      const scatterData = companyData.map(item => {
        return {
          name: item.company_name,
          value: [item.company_name, item.position_count],
          symbolSize: item.percentage * 2,
          itemStyle: {
            color: '#3eabff',
            opacity: 0.8
          }
        }
      });
      
      // 提取公司名称作为X轴数据
      const companies = companyData.map(item => item.company_name);
      
      // 计算Y轴最大值
      const maxPositionCount = Math.ceil(Math.max(...companyData.map(item => item.position_count)) / 50) * 50;
      const interval = maxPositionCount > 300 ? 50 : 30;
      
      // 更新图表配置
      this.scatterChart.setOption({
        xAxis: {
          data: companies
        },
        yAxis: {
          max: maxPositionCount,
          interval: interval
        },
        series: [
          {
            data: scatterData
          }
        ]
      });
    },
    
    initScatter() {
      // 初始化散点图
      this.scatterChart = echarts.init(this.$refs.companyHeatScatterRef)
      
      // 从后端获取数据
      this.getCompanyData().then(companyData => {
        if (!companyData || companyData.length === 0) {
          console.error('获取公司数据失败或数据为空')
          return
        }
        
        // 处理数据为散点图格式
        const scatterData = companyData.map(item => {
          return {
            name: item.company_name,
            value: [item.company_name, item.position_count],
            symbolSize: item.percentage * 2,
            itemStyle: {
              color: '#3eabff',
              opacity: 0.8
            }
          }
        })
        
        // 提取公司名称作为X轴数据
        const companies = companyData.map(item => item.company_name)
        
        // 计算Y轴最大值
        const maxPositionCount = Math.ceil(Math.max(...companyData.map(item => item.position_count)) / 50) * 50
        const interval = maxPositionCount > 300 ? 50 : 30
        
        // 配置散点图选项
        const option = {
          title: {
            text: '',
            left: 'center',
            top: 0,
            textStyle: {
              color: '#fff'
            }
          },
          tooltip: {
            trigger: 'item',
            formatter: function(params) {
              return '公司: ' + params.value[0] + '<br>' +
                     '职位数量: ' + params.value[1] + '<br>' +
                     '占比: ' + (params.symbolSize / 2).toFixed(2) + '%'
            }
          },
          grid: {
            left: '5%',
            right: '5%',
            bottom: '15%',
            top: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: companies,
            axisLabel: {
              color: '#fff',
              interval: 0,
              rotate: 30,
              fontSize: 10
            },
            axisLine: {
              lineStyle: {
                color: '#fff'
              }
            },
            splitLine: {
              show: true,
              lineStyle: {
                color: 'rgba(255, 255, 255, 0.1)',
                type: 'dashed'
              }
            }
          },
          yAxis: {
            type: 'value',
            name: '职位数量',
            nameTextStyle: {
              color: '#fff',
              fontSize: 12
            },
            min: 0,
            max: maxPositionCount,
            interval: interval,
            axisLabel: {
              color: '#fff'
            },
            axisLine: {
              lineStyle: {
                color: '#fff'
              }
            },
            splitLine: {
              lineStyle: {
                color: 'rgba(255, 255, 255, 0.1)',
                type: 'dashed'
              }
            }
          },
          series: [
            {
              name: '公司热度',
              type: 'scatter',
              data: scatterData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(62, 171, 255, 0.5)'
                }
              }
            }
          ]
        }
        
        // 应用配置
        this.scatterChart.setOption(option)
      })
      
      // 窗口大小变化时自动调整图表大小
      this.resizeHandler = () => {
        this.scatterChart.resize()
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    
    // 获取公司数据
    async getCompanyData() {
      try {
        const response = await axios.get('http://localhost:9090/loadChart/getTop10Company')
        if (response.data.code === 1) {
          return response.data.data
        }
        return []
      } catch (error) {
        console.error('获取公司数据失败:', error)
        return []
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