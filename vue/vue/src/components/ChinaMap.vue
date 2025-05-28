<template>
  <div class="chart" ref="chinaMapRef"></div>
</template>

<script>
import * as echarts from 'echarts'
import axios from 'axios'

export default {
  name: 'ChinaMap',
  data() {
    return {
      mapChart: null,
      resizeHandler: null,
      salaryData: [],
      provinceSalaryData: [],
      timerId: null
    }
  },
  // 页面初始化调用数据更新的方法
  created() {
    // 获取数据
    this.getData();
    // 定时更新数据
    this.refresh();
  },
  mounted() {
    this.$nextTick(() => {
      // 先获取薪资数据，再初始化地图
      this.getCityAverageSalary().then(() => {
        this.initMap()
      })
    })
  },
  beforeUnmount() {
    if (this.mapChart) {
      this.mapChart.dispose()
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
      // 调用获取城市平均薪资数据的方法
      this.getCityAverageSalary().then(() => {
        // 如果地图已经初始化，则更新数据
        if (this.mapChart) {
          this.mapChart.setOption({
            series: [{
              data: this.provinceSalaryData
            }]
          });
        }
      });
    },
    
    // 用于定期刷新
    refresh() {
      this.timerId = setTimeout(() => {
        this.getData();
        this.refresh();
      }, 1 * 5 * 1000); // 刷新时间间隔
    },
    
    // 获取城市平均薪资数据并转换为省份数据
    async getCityAverageSalary() {
      try {
        const response = await axios.post('http://localhost:9090/loadChart/getCityAverageSalary')
        if (response.data.code === 1) {
          this.salaryData = response.data.data.map(item => {
            // 处理城市名称，确保与地图数据匹配
            let cityName = item.cityName
            if (!cityName.endsWith('市') && !cityName.endsWith('省') && cityName !== '海外') {
              cityName += '市'
            }
            return {
              name: cityName,
              value: parseFloat(item.avgSalary.toFixed(1)),
              province: this.getCityProvince(item.cityName)
            }
          })
          
          // 计算各省平均薪资
          this.calculateProvinceSalary()
        } else {
          console.error('获取薪资数据失败:', response.data.msg)
        }
      } catch (error) {
        console.error('请求薪资数据出错:', error)
      }
    },
    
    // 根据城市名获取所属省份
    getCityProvince(cityName) {
      // 直辖市直接返回
      if (['北京', '上海', '天津', '重庆'].includes(cityName)) {
        return cityName + '市'
      }
      
      // 特别行政区
      if (['香港', '澳门'].includes(cityName)) {
        return cityName + '特别行政区'
      }
      
      // 城市与省份对应关系
      const cityToProvince = {
        // 广东省
        '深圳': '广东省', '广州': '广东省', '佛山': '广东省', '东莞': '广东省', 
        '中山': '广东省', '珠海': '广东省', '惠州': '广东省', '湛江': '广东省',
        '汕头': '广东省', '江门': '广东省', '茂名': '广东省', '肇庆': '广东省',
        '梅州': '广东省', '汕尾': '广东省', '河源': '广东省', '阳江': '广东省',
        '清远': '广东省', '韶关': '广东省', '揭阳': '广东省', '云浮': '广东省',
        '潮州': '广东省',
        
        // 江苏省
        '南京': '江苏省', '苏州': '江苏省', '常州': '江苏省', '无锡': '江苏省', 
        '泰州': '江苏省', '镇江': '江苏省', '扬州': '江苏省', '南通': '江苏省',
        '徐州': '江苏省', '连云港': '江苏省', '淮安': '江苏省', '盐城': '江苏省',
        '宿迁': '江苏省',
        
        // 浙江省
        '杭州': '浙江省', '宁波': '浙江省', '金华': '浙江省', '嘉兴': '浙江省',
        '温州': '浙江省', '台州': '浙江省', '绍兴': '浙江省', '湖州': '浙江省',
        '丽水': '浙江省', '衢州': '浙江省', '舟山': '浙江省',
        
        // 福建省
        '福州': '福建省', '厦门': '福建省', '泉州': '福建省', '宁德': '福建省',
        '莆田': '福建省', '漳州': '福建省', '龙岩': '福建省', '三明': '福建省',
        '南平': '福建省',
        
        // 四川省
        '成都': '四川省', '绵阳': '四川省', '自贡': '四川省', '攀枝花': '四川省',
        '泸州': '四川省', '德阳': '四川省', '广元': '四川省', '遂宁': '四川省',
        '内江': '四川省', '乐山': '四川省', '南充': '四川省', '眉山': '四川省',
        '宜宾': '四川省', '广安': '四川省', '达州': '四川省', '雅安': '四川省',
        '巴中': '四川省', '资阳': '四川省', '阿坝': '四川省', '甘孜': '四川省',
        '凉山': '四川省',
        
        // 湖北省
        '武汉': '湖北省', '黄石': '湖北省', '十堰': '湖北省', '宜昌': '湖北省',
        '襄阳': '湖北省', '鄂州': '湖北省', '荆门': '湖北省', '孝感': '湖北省',
        '荆州': '湖北省', '黄冈': '湖北省', '咸宁': '湖北省', '随州': '湖北省',
        '恩施': '湖北省', '仙桃': '湖北省', '潜江': '湖北省', '天门': '湖北省',
        
        // 吉林省
        '长春': '吉林省', '吉林': '吉林省', '四平': '吉林省', '辽源': '吉林省',
        '通化': '吉林省', '白山': '吉林省', '松原': '吉林省', '白城': '吉林省',
        '延边': '吉林省',
        
        // 河南省
        '郑州': '河南省', '开封': '河南省', '洛阳': '河南省', '平顶山': '河南省',
        '安阳': '河南省', '鹤壁': '河南省', '新乡': '河南省', '焦作': '河南省',
        '濮阳': '河南省', '许昌': '河南省', '漯河': '河南省', '三门峡': '河南省',
        '南阳': '河南省', '商丘': '河南省', '信阳': '河南省', '周口': '河南省',
        '驻马店': '河南省', '济源': '河南省',
        
        // 陕西省
        '西安': '陕西省', '铜川': '陕西省', '宝鸡': '陕西省', '咸阳': '陕西省',
        '渭南': '陕西省', '延安': '陕西省', '汉中': '陕西省', '榆林': '陕西省',
        '安康': '陕西省', '商洛': '陕西省',
        
        // 广西壮族自治区
        '南宁': '广西壮族自治区', '柳州': '广西inant自治区', '桂林': '广西inant自治区',
        '梧州': '广西inant自治区', '北海': '广西inant自治区', '防城港': '广西inant自治区',
        '钦州': '广西inant自治区', '贵港': '广西inant自治区', '玉林': '广西inant自治区',
        '百色': '广西inant自治区', '贺州': '广西inant自治区', '河池': '广西inant自治区',
        '来宾': '广西inant自治区', '崇左': '广西inant自治区',
        
        // 湖南省
        '长沙': '湖南省', '株洲': '湖南省', '湘潭': '湖南省', '衡阳': '湖南省',
        '邵阳': '湖南省', '岳阳': '湖南省', '常德': '湖南省', '张家界': '湖南省',
        '益阳': '湖南省', '郴州': '湖南省', '永州': '湖南省', '怀化': '湖南省',
        '娄底': '湖南省', '湘西': '湖南省',
        
        // 贵州省
        '贵阳': '贵州省', '六盘水': '贵州省', '遵义': '贵州省', '安顺': '贵州省',
        '毕节': '贵州省', '铜仁': '贵州省', '黔西南': '贵州省', '黔东南': '贵州省',
        '黔南': '贵州省',
        
        // 云南省
        '昆明': '云南省', '曲靖': '云南省', '玉溪': '云南省', '保山': '云南省',
        '昭通': '云南省', '丽江': '云南省', '普洱': '云南省', '临沧': '云南省',
        '楚雄': '云南省', '红河': '云南省', '文山': '云南省', '西双版纳': '云南省',
        '大理': '云南省', '德宏': '云南省', '怒江': '云南省', '迪庆': '云南省',
        
        // 山东省
        '济南': '山东省', '青岛': '山东省', '淄博': '山东省', '枣庄': '山东省',
        '东营': '山东省', '烟台': '山东省', '潍坊': '山东省', '济宁': '山东省',
        '泰安': '山东省', '威海': '山东省', '日照': '山东省', '临沂': '山东省',
        '德州': '山东省', '聊城': '山东省', '滨州': '山东省', '菏泽': '山东省',
        
        // 辽宁省
        '沈阳': '辽宁省', '大连': '辽宁省', '鞍山': '辽宁省', '抚顺': '辽宁省',
        '本溪': '辽宁省', '丹东': '辽宁省', '锦州': '辽宁省', '营口': '辽宁省',
        '阜新': '辽宁省', '辽阳': '辽宁省', '盘锦': '辽宁省', '铁岭': '辽宁省',
        '朝阳': '辽宁省', '葫芦岛': '辽宁省',
        
        // 河北省
        '石家庄': '河北省', '唐山': '河北省', '秦皇岛': '河北省', '邯郸': '河北省',
        '邢台': '河北省', '保定': '河北省', '张家口': '河北省', '承德': '河北省',
        '沧州': '河北省', '廊坊': '河北省', '衡水': '河北省',
        
        // 安徽省
        '合肥': '安徽省', '芜湖': '安徽省', '蚌埠': '安徽省', '淮南': '安徽省',
        '马鞍山': '安徽省', '淮北': '安徽省', '铜陵': '安徽省', '安庆': '安徽省',
        '黄山': '安徽省', '滁州': '安徽省', '阜阳': '安徽省', '宿州': '安徽省',
        '六安': '安徽省', '亳州': '安徽省', '池州': '安徽省', '宣城': '安徽省',
        
        // 江西省
        '南昌': '江西省', '景德镇': '江西省', '萍乡': '江西省', '九江': '江西省',
        '新余': '江西省', '鹰潭': '江西省', '赣州': '江西省', '吉安': '江西省',
        '宜春': '江西省', '抚州': '江西省', '上饶': '江西省',
        
        // 山西省
        '太原': '山西省', '大同': '山西省', '阳泉': '山西省', '长治': '山西省',
        '晋城': '山西省', '朔州': '山西省', '晋中': '山西省', '运城': '山西省',
        '忻州': '山西省', '临汾': '山西省', '吕梁': '山西省',
        
        // 黑龙江省
        '哈尔滨': '黑龙江省', '齐齐哈尔': '黑龙江省', '鸡西': '黑龙江省', '鹤岗': '黑龙江省',
        '双鸭山': '黑龙江省', '大庆': '黑龙江省', '伊春': '黑龙江省', '佳木斯': '黑龙江省',
        '七台河': '黑龙江省', '牡丹江': '黑龙江省', '黑河': '黑龙江省', '绥化': '黑龙江省',
        '大兴安岭': '黑龙江省',
        
        // 内蒙古自治区
        '呼和浩特': '内蒙古自治区', '包头': '内蒙古自治区', '乌海': '内蒙古自治区',
        '赤峰': '内蒙古自治区', '通辽': '内蒙古自治区', '鄂尔多斯': '内蒙古自治区',
        '呼伦贝尔': '内蒙古自治区', '巴彦淖尔': '内蒙古自治区', '乌兰察布': '内蒙古自治区',
        '兴安盟': '内蒙古自治区', '锡林郭勒盟': '内蒙古自治区', '阿拉善盟': '内蒙古自治区',
        
        // 甘肃省
        '兰州': '甘肃省', '嘉峪关': '甘肃省', '金昌': '甘肃省', '白银': '甘肃省',
        '天水': '甘肃省', '武威': '甘肃省', '张掖': '甘肃省', '平凉': '甘肃省',
        '酒泉': '甘肃省', '庆阳': '甘肃省', '定西': '甘肃省', '陇南': '甘肃省',
        '临夏': '甘肃省', '甘南': '甘肃省',
        
        // 宁夏回族自治区
        '银川': '宁夏回族自治区', '石嘴山': '宁夏回族自治区', '吴忠': '宁夏回族自治区',
        '固原': '宁夏回族自治区', '中卫': '宁夏回族自治区',
        
        // 青海省
        '西宁': '青海省', '海东': '青海省', '海北': '青海省', '黄南': '青海省',
        '海南': '青海省', '果洛': '青海省', '玉树': '青海省', '海西': '青海省',
        
        // 新疆维吾尔自治区
        '乌鲁木齐': '新疆维吾尔自治区', '克拉玛依': '新疆维吾尔自治区', '吐鲁番': '新疆维吾尔自治区',
        '哈密': '新疆维吾尔自治区', '昌吉': '新疆维吾尔自治区', '博尔塔拉': '新疆维吾尔自治区',
        '巴音郭楞': '新疆维吾尔自治区', '阿克苏': '新疆维吾尔自治区', '克孜勒苏': '新疆维吾尔自治区',
        '喀什': '新疆维吾尔自治区', '和田': '新疆维吾尔自治区', '伊犁': '新疆维吾尔自治区',
        '塔城': '新疆维吾尔自治区', '阿勒泰': '新疆维吾尔自治区',
        
        // 西藏自治区
        '拉萨': '西藏自治区', '日喀则': '西藏自治区', '昌都': '西藏自治区',
        '林芝': '西藏自治区', '山南': '西藏自治区', '那曲': '西藏自治区',
        '阿里': '西藏自治区',
        
        // 海南省
        '海口': '海南省', '三亚': '海南省', '三沙': '海南省', '儋州': '海南省',
        '五指山': '海南省', '琼海': '海南省', '文昌': '海南省', '万宁': '海南省',
        '东方': '海南省', '定安': '海南省', '屯昌': '海南省', '澄迈': '海南省',
        '临高': '海南省', '白沙': '海南省', '昌江': '海南省', '乐东': '海南省',
        '陵水': '海南省', '保亭': '海南省', '琼中': '海南省',
        
        // 海外
        '海外': '海外'
      }
      
      return cityToProvince[cityName] || '未知'
    },
    
    // 计算各省平均薪资
    calculateProvinceSalary() {
      const provinceData = {}
      
      // 按省份分组城市数据
      this.salaryData.forEach(item => {
        if (item.province !== '未知' && item.province !== '海外') {
          if (!provinceData[item.province]) {
            provinceData[item.province] = {
              totalSalary: 0,
              count: 0
            }
          }
          provinceData[item.province].totalSalary += item.value
          provinceData[item.province].count += 1
        }
      })
      
      // 计算各省平均薪资
      this.provinceSalaryData = Object.keys(provinceData).map(province => {
        const avgSalary = provinceData[province].totalSalary / provinceData[province].count
        return {
          name: province,
          value: parseFloat(avgSalary.toFixed(1))
        }
      })
    },
    
    initMap() {
      // 初始化地图
      this.mapChart = echarts.init(this.$refs.chinaMapRef)
      this.mapChart.showLoading()
      
      // 获取地图JSON数据
      fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
        .then(response => response.json())
        .then(geoJson => {
          this.mapChart.hideLoading()
          
          // 注册地图数据
          echarts.registerMap('China', geoJson)
          
          // 计算薪资范围
          let minSalary = 1.0  // 默认最小值
          let maxSalary = 10.0 // 默认最大值
          
          if (this.provinceSalaryData.length > 0) {
            const values = this.provinceSalaryData.map(item => item.value)
            minSalary = Math.floor(Math.min(...values))
            maxSalary = Math.ceil(Math.max(...values))
          }
          
          // 配置地图选项
          const option = {
            title: {
              text: '',
              left: 'center',
              textStyle: {
                color: '#fff',
                fontSize: 16,
                textShadow: '0 0 10px rgba(62, 171, 255, 0.7)'
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
            visualMap: {
              type: 'continuous',
              min: minSalary,
              max: maxSalary,
              left: 20,
              bottom: 20,
              itemWidth: 15,
              itemHeight: 120,
              text: ['高', '低'],
              calculable: true,
              inRange: {
                color: ['#3eabff', '#2a7ad9', '#1e5bb8', '#183c7a']
              },
              textStyle: {
                color: '#fff',
                textShadow: '0 0 5px rgba(62, 171, 255, 0.5)'
              }
            },
            series: [{
              name: '省份平均薪资',
              type: 'map',
              map: 'China',
              roam: true,
              zoom: 1.2,
              center: [104, 36],
              selectedMode: false,
              emphasis: {
                label: {
                  show: true,
                  color: '#fff',
                  textShadow: '0 0 5px rgba(62, 171, 255, 0.8)'
                },
                itemStyle: {
                  areaColor: '#3eabff',
                  shadowBlur: 15,
                  shadowColor: 'rgba(62, 171, 255, 0.8)'
                }
              },
              itemStyle: {
                areaColor: '#4a6bae', 
                borderColor: '#2a4580',
                borderWidth: 1,
                shadowColor: 'rgba(0, 100, 255, 0.3)',
                shadowBlur: 5
              },
              label: {
                show: false,
                color: '#fff',
                fontSize: 12
              },
              data: this.provinceSalaryData
            }],
            backgroundColor: 'transparent'
          }
          
          // 应用配置
          this.mapChart.setOption(option)
          
          // 窗口大小变化时自动调整图表大小
          this.resizeHandler = () => {
            this.mapChart.resize()
          }
          window.addEventListener('resize', this.resizeHandler)
        })
        .catch(error => {
          console.error('加载地图数据失败:', error)
          this.mapChart.hideLoading()
          
          // 显示错误信息
          if (this.$refs.chinaMapRef) {
            this.$refs.chinaMapRef.innerHTML = '<div style="text-align:center;color:#fff;padding-top:50px;">地图数据加载失败，请检查网络连接</div>'
          }
        })
    }
  }
}
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
  min-height: 300px;
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