<template>
  <div class="main">
    <div class="head">
      <div class="title">招聘数据分析看板</div>
      <div id="currentDateTime">{{ currentTime }}</div>
    </div>
    
    <!-- 内容盒子 -->
    <div class="content">
      <!-- 左侧内容 -->
      <div class="content-left">
        <!-- 热门技能TOP10词云 -->
        <div class="panel">
          <div class="p-title">热门技能TOP10词云</div>
          <div class="p-content">
            <SkillWordCloud />
          </div>
        </div>
        
        <!-- 学历要求分布 -->
        <!-- 在左侧内容区域的学历要求分布面板中使用 -->
        <div class="panel">
          <div class="p-title">学历要求分布</div>
          <div class="p-content">
            <EducationFunnel />
          </div>
        </div>
        
        <!-- 学历-平均薪资分布 -->
        <div class="panel">
          <div class="p-title">学历-平均薪资分布</div>
          <div class="p-content">
            <EducationSalary />
          </div>
        </div>
      </div>
      
      <!-- 中间内容 -->
      <div class="content-center">
        <!-- 各市平均薪资图 -->
        <div class="panel">
          <div class="p-title">各市平均薪资图</div>
          <div class="p-content">
            <ChinaMap />
          </div>
        </div>
        
        <!-- 城市-职位需求关系 -->
        <div class="panel">
          <div class="p-title">城市-职位需求关系</div>
          <div class="p-content">
            <CityJobHeatmap />
          </div>
        </div>
      </div>
      
      <!-- 右侧内容 -->
      <div class="content-right">
        <!-- 城市-薪资占比排行 -->
        <div class="panel">
          <div class="p-title">城市-薪资占比排行</div>
          <div class="p-content">
            <CitySalaryBar />
          </div>
        </div>
        
        <!-- 公司热度 -->
        <div class="panel">
          <div class="p-title">公司热度</div>
          <div class="p-content">
            <CompanyHeatScatter />
          </div>
        </div>
        
        <!-- 学历-最小薪资分布 -->
        <div class="panel">
          <div class="p-title">城市-经验关系</div>
          <div class="p-content">
            <EducationMinSalary />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CityJobHeatmap from '@/components/CityJobHeatmap.vue'
import ChinaMap from '@/components/ChinaMap.vue'
import SkillWordCloud from '@/components/SkillWordCloud.vue'
import EducationFunnel from '@/components/EducationFunnel.vue'
import EducationSalary from '@/components/EducationSalary.vue'
import CityLinkedSalary from '@/components/CityLinkedSalary.vue'
import CompanyHeatScatter from '@/components/CompanyHeatScatter.vue'
import CitySalaryBar from '@/components/CitySalaryBar.vue'

export default {
  name: 'Home',
  components: {
    ChinaMap,
    SkillWordCloud,
    EducationFunnel,
    EducationSalary,
    CityJobHeatmap,
    EducationMinSalary: CityLinkedSalary,
    CompanyHeatScatter,
    CitySalaryBar
  },
  data() {
    return {
      currentTime: ''
    }
  },
  mounted() {
    this.updateTime()
    setInterval(this.updateTime, 1000)
  },
  methods: {
    updateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.main {
  height: 100vh;
  width: 100%;
  display: grid;
  grid-template-rows: auto 1fr;
  grid-template-columns: repeat(1, 1fr);
  gap: 0.5rem; /* 减小间距 */
  color: #fff;
  background: #010e50 url(@/assets/images/bg.jpg) center top no-repeat;
  background-size: 100% auto;
  overflow: hidden; /* 防止滚动 */
}

#currentDateTime {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  z-index: 1;
  opacity: 0.8;
}

.head .title {
  text-shadow: 3px 3px 3px rgba(0, 0, 0, .3);
  text-align: center;
  line-height: 80px;
  letter-spacing: 4px;
  font-size: 40px;
  font-weight: bolder;
}

.content {
  display: grid;
  grid-template-rows: repeat(1, 1fr);
  grid-template-columns: 25% 1fr 25%;
  gap: 0.3rem; /* 减小间距 */
  padding: 0 0.3rem 0.3rem 0.3rem; /* 减小内边距 */
  overflow: hidden; /* 防止滚动 */
}

.content .content-left {
  display: grid;
  grid-template-rows: 38% 1fr 1fr;
  grid-template-columns: repeat(1, 1fr);
  gap: 0.3rem; /* 减小间距 */
  overflow: hidden; /* 防止滚动 */
}

.content .content-center {
  display: grid;
  grid-template-rows: 62% 1fr;
  grid-template-columns: repeat(1, 1fr);
  gap: 0.3rem; /* 减小间距 */
  overflow: hidden; /* 防止滚动 */
}

.content .content-right {
  display: grid;
  grid-template-rows: repeat(3, 1fr);
  grid-template-columns: repeat(1, 1fr);
  gap: 0.3rem; /* 减小间距 */
  overflow: hidden; /* 防止滚动 */
}

.panel {
  border: 1px solid rgba(100, 162, 255, .2);
  position: relative;
  background: rgba(0, 35, 120, .36);
  display: flex;
  flex-direction: column;
}

.panel:after, .panel:before {
  position: absolute;
  content: "";
  width: 20px;
  height: 30px;
}

.panel:before {
  border-left: 1px solid #0258f0;
  border-top: 1px solid #0258f0;
  left: -1px;
  top: -1px;
}

.panel:after {
  border-right: 1px solid #0258f0;
  border-bottom: 1px solid #0258f0;
  right: -1px;
  bottom: -1px;
}

.panel .p-title {
  height: 30px; /* 减小标题高度 */
  font-size: 16px; /* 减小字体大小 */
  line-height: 30px;
  width: 100%;
  font-weight: 600;
  padding-left: 15px;
  position: relative;
  flex-grow: 0;
}

.panel .p-title:after {
  position: absolute;
  content: "";
  width: 20%;
  height: 1px;
  background: #0258f0;
  left: 5%;
  opacity: 0.4;
  bottom: 0;
}

.panel .p-content {
  flex-grow: 1;
  margin: 0.2rem 0.2rem 0.2rem 0.2rem;
}

.plain-panel {
  border: 0;
  background: none;
}

.plain-panel::before,
.plain-panel::after {
  border: 0;
}

.chart {
  width: 100%;
  height: 100%;
  overflow: hidden; /* 防止图表内部滚动 */
}

.chart:empty {
  border: 1px dashed #ccc;
  opacity: 0.3;
}

.chart:empty::after {
  content: "图表";
  position: absolute;
  color: white;
  top: 50%;
  left: 50%;
  margin-top: -1rem;
  margin-left: -1rem;
}
</style>