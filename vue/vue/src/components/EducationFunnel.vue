<template>
  <div class="chart" ref="educationFunnelRef"></div>
</template>

<script>
import * as echarts from 'echarts'
import axios from "axios";

export default {
  name: 'EducationFunnel',
  data() {
    return {
      funnelChart: null,
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
      this.getEducationAndOccurrenceCount().then(data => {
        this.initFunnel(data)
      })
    })
    // 移除原有的 setInterval 定时器
    // setInterval(() => {
    //   this.getEducationAndOccurrenceCount().then(data => {
    //     this.initFunnel(data)
    //   })
    // }, 1000 * 10)
  },
  beforeUnmount() {
    if (this.funnelChart) {
      this.funnelChart.dispose()
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
      this.getEducationAndOccurrenceCount().then(data => {
        if (data && data.length > 0) {
          if (this.funnelChart) {
            this.updateFunnel(data);
          } else {
            this.initFunnel(data);
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
    
    // 更新漏斗图数据
    updateFunnel(data) {
      const color = ['#3eabff','#20cc98', '#33b565', '#205bcf'];
      let count = 0;
      //计算出所有职位的数量
      const sum = data.reduce((acc, cur) => acc + cur.job_count, 0);
      //数据源
      const educationData = data.map(item => {
        count++;
        return {
          value: Math.round(item.job_count / sum * 100),
          name: item.education_level,
          itemStyle: {
            color: color[count-1],
            shadowColor: 'rgba(62, 171, 255, 0.5)',
            shadowBlur: 10
          }
        }
      });
      
      // 更新漏斗图配置
      this.funnelChart.setOption({
        series: [
          {
            data: educationData
          }
        ]
      });
    },
    
    initFunnel(data) {
      // 初始化漏斗图
      this.funnelChart = echarts.init(this.$refs.educationFunnelRef)
      const color = ['#3eabff','#20cc98', '#33b565', '#205bcf'];
      let count = 0;
      //计算出所有职位的数量
      const sum = data.reduce((acc, cur) => acc + cur.job_count, 0);
      //数据源
      const educationData = data.map(item => {
        count++;
        return {
          value: Math.round(item.job_count / sum * 100),
          name: item.education_level,
          itemStyle: {
            color: color[count-1],
            shadowColor: 'rgba(62, 171, 255, 0.5)',
            shadowBlur: 10
          }
        }
      })
      // 配置漏斗图选项
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
          trigger: 'item',
          formatter: '{b} : {c}%',
          backgroundColor: 'rgba(10, 20, 60, 0.8)',
          borderColor: '#3eabff',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          extraCssText: 'box-shadow: 0 0 10px rgba(62, 171, 255, 0.5);'
        },
        legend: {
          top: '10%',
          left: 'center',
          data: ['本科', '大专', '不限', '硕士'],
          textStyle: {
            color: '#fff',
            textShadow: '0 0 5px rgba(62, 171, 255, 0.5)'
          }
        },
        series: [
          {
            name: '学历要求',
            type: 'funnel',
            left: '10%',
            top: 60,
            bottom: 10,
            width: '80%',
            min: 0,
            max: 100,
            minSize: '0%',
            maxSize: '100%',
            sort: 'descending',
            gap: 2,
            label: {
              show: true,
              position: 'inside',
              color: '#fff',
              formatter: '{b} {c}%',
              textShadow: '0 0 5px rgba(0, 0, 0, 0.5)'
            },
            labelLine: {
              length: 10,
              lineStyle: {
                width: 1,
                type: 'solid',
                color: 'rgba(255, 255, 255, 0.7)'
              }
            },
            itemStyle: {
              borderColor: '#062d96',
              borderWidth: 1
            },
            emphasis: {
              label: {
                fontSize: 20,
                textShadow: '0 0 8px rgba(0, 0, 0, 0.8)'
              },
              itemStyle: {
                shadowBlur: 20,
                shadowColor: 'rgba(62, 171, 255, 0.8)'
              }
            },
            data: educationData
          }
        ]
      }
      
      // 应用配置
      this.funnelChart.setOption(option)
      
      // 窗口大小变化时自动调整图表大小
      this.resizeHandler = () => {
        this.funnelChart.resize()
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    //发起请求，后端处理并返回数据
    async getEducationAndOccurrenceCount () {
      const res = await axios.get('http://localhost:9090/loadChart/getEducationLevelAndJobCount')
      if (res.data.code === 1){
        console.log(res.data.data)
        return res.data.data
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