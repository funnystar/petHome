<template>
  <div>
    <div class="card" style="padding: 15px">
      您好，{{ user?.name }}！欢迎使用本系统
    </div>

    <div style="display: flex; margin: 10px 0">
      <div style="width: 100%;" class="card">
        <!-- 系统公告区 -->
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">公告列表</div>
        <div >
          <el-timeline  reverse slot="reference">
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
        <!--   宠物卡片区     -->
        <div style="margin-bottom: 20px; font-size: 20px; font-weight: bold; color: #817a70">如果你喜欢我，就领养我吧~</div>
        <div>
          <el-row>
            <el-col :span="5" v-for="item in animalData">
              <div class="card" style="background-color: #f8f4e8; text-align: center">
                <img :src="item.img" alt="" style="width: 80px; height: 80px; border-radius: 50%">
                <div style="font-weight: bold; color: #817a70; margin-top: 5px">{{ item.name }}（{{ item.type }}）</div>
                <div style="color: #575353; font-size: 13px; margin-top: 5px">性别：{{ item.sex }}， 年龄：{{ item.age }}， <span style="color: #7d3d0c">{{ item.status }}</span></div>
                <div style="margin-top: 15px; color: #4b4949; text-align: left">认识一下我：{{ item.descr }}</div>
                <div>
                  <el-button type="success" size="mini" style="margin-top: 20px" :disabled="item.status === '已领养'" @click="adopt(item)">领养</el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import adopt from "@/views/manager/Adopt.vue";

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],
      animalData:[],
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
    this.loadAnimal()
  },
  methods: {
    loadAnimal() {
      this.$request.get('/animal/selectAll').then(res =>{
        if (res.code === '200'){
          this.animalData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    adopt(animal) {
      // 判断如果为管理员用户不能使用领养操作
      if (this.user.role === 'ADMIN') {
        this.$message.warning('您的角色不支持该操作')
        return
      }
      let data = JSON.parse(JSON.stringify(animal))
      data.status = '已领养'
      this.$request.put('/animal/update',data).then(res => {
        if (res.code === '200') {
          this.loadAnimal()
          // 在领养记录同步一条信息
          let adoptData = {
            userId: this.user.id,
            animalId:data.id
          }
          this.$request.post('/adopt/add',adoptData).then(res => {
            if (res.code === '200') {
              this.$message.success('领养成功，请好好善待它哦~')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.el-col-5{
  width: 20%;
  max-width:20%;
  padding: 10px 10px;
}
</style>