<template>
  <div class="chart" ref="skillWordCloudRef"></div>
</template>

<script>
import * as echarts from 'echarts'
import 'echarts-wordcloud'
import axios from 'axios'
export default {
  name: 'SkillWordCloud',
  data() {
    return {
      wordCloudChart: null,
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
      this.getSkillNameAndOccurrenceCount().then(data => {
        this.initWordCloud(data)
      })
    })
  },
  beforeUnmount() {
    if (this.wordCloudChart) {
      this.wordCloudChart.dispose()
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
      this.getSkillNameAndOccurrenceCount().then(data => {
        if (data && data.length > 0) {
          if (this.wordCloudChart) {
            this.updateWordCloud(data);
          } else {
            this.initWordCloud(data);
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
    
    // 更新词云图数据
    updateWordCloud(data) {
      const name = data.map(item => item.skill_name);
      const count = data.map(item => item.occurrence_count);
      // 测试的技能数据
      const skillData = name.map((name, index) => ({
        name: name,
        value: count[index]
      }));
      
      // 更新词云图配置
      this.wordCloudChart.setOption({
        series: [{
          data: skillData
        }]
      });
    },

    initWordCloud(data) {
      // 初始化词云图
      this.wordCloudChart = echarts.init(this.$refs.skillWordCloudRef)

      const name = data.map(item => item.skill_name);
      const count = data.map(item => item.occurrence_count);
      // 测试的技能数据
      const skillData = name.map((name, index) => ({
        name: name,
        value: count[index]
      }));
      console.log(skillData);

      
      // 配置词云图选项
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
          show: true,
          backgroundColor: 'rgba(10, 20, 60, 0.8)',
          borderColor: '#3eabff',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          extraCssText: 'box-shadow: 0 0 10px rgba(62, 171, 255, 0.5);',
          formatter: function(item) {
            return item.name + ': ' + item.value
          }
        },
        series: [{
          type: 'wordCloud',
          shape: 'circle',
          left: 'center',
          top: 'center',
          width: '90%',
          height: '90%',
          right: null,
          bottom: null,
          sizeRange: [12, 50],
          rotationRange: [-45, 45],
          rotationStep: 15,
          gridSize: 8,
          drawOutOfBound: false,
          textStyle: {
            fontFamily: 'sans-serif',
            fontWeight: 'bold',
            color: function() {
                    return 'rgb(' + 
                        Math.round(Math.random() * 50) + ',' + 
                        Math.round(Math.random() * 100 + 100) + ',' + 
                        Math.round(Math.random() * 50 + 200) + ')';
                },
            textShadow: '0 0 3px rgba(0, 0, 0, 0.3)'
          },
          emphasis: {
            textStyle: {
              shadowBlur: 20,
              shadowColor: '#3eabff',
              fontSize: '+=5'
            }
          },
          data: skillData
        }]
      }
      
      // 应用配置
      this.wordCloudChart.setOption(option)
      
      // 窗口大小变化时自动调整图表大小
      this.resizeHandler = () => {
        this.wordCloudChart.resize()
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    //发起请求，后端处理并返回数据（技能名称和出现的次数）
    async getSkillNameAndOccurrenceCount (){
      console.log(sessionStorage.getItem('token'));
      return await axios.get('http://localhost:9090/loadChart/getSkillNameAndOccurrenceCount')
          .then(response => {
            return response.data.data;
          })
          .catch(error => {
            console.error('Failed to fetch skill data:', error);
            return [];
          });
    },
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