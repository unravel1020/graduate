<template xmlns="http://www.w3.org/1999/html">
  <div class="common-layout">
    <el-container>
      <el-header style=" height: 60px;background: rgba(255, 255, 255, 0);box-shadow: 0 0 10px 2px #e1c8f8;padding: 0 20px;display: flex;justify-content: space-between;align-items: center;">
        <!-- 左侧占位 -->
        <div style="display: flex; align-items: center; padding-left: 10px; ">
          <img src="../assets/css/imgs/express-delivery.png" style="width: 40px; margin-right: 10px;" />
          <span style="color: #fffdfd">高校快递系统</span>
        </div>
        <!-- 公告居中 -->
        <div style="position: absolute; left: 50%; transform: translateX(-50%); width: 800px; height: 40px; display: flex; align-items: center; overflow: hidden;">
          <el-carousel
              style="width: 100%;color: #fffdfd;padding-top: 20px;padding-left: 40px"
              height="40px"
              direction="vertical"
              :autoplay="true"
              :interval="4000"
              :pause-on-hover="true"
              indicator-position="none"
              arrow="never"
          >
            <el-carousel-item
                v-for="notice in notices"
                :key="notice.noticeId"
            >
              <div class="notice-text" style="font-size: 20px; white-space: nowrap;">
                <strong>{{ notice.title }}：</strong>{{ notice.content }}
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
        <div class="avatar-container" ref="avatarContainer">
          <el-avatar
              :size="50"
              :src="fullAvatarUrl"
              class="avatar"
              @click="toggleDropdown"
          />
          <transition name="el-fade-in">
            <div
                v-show="showDropdown"
                class="dropdown-menu"
                @click.stop
            >
              <div class="dropdown-item" @click="commentDialogVisible = true;">
                <el-icon><EditPen /></el-icon>
                <span>系统评价</span>
              </div>
              <el-divider class="dropdown-divider" />
              <div class="dropdown-item" @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                <span>退出登录</span>
              </div>
            </div>
          </transition>
        </div>
      </el-header>
    </el-container>
    <!--评论对话框-->
    <el-dialog v-model="commentDialogVisible" title="发表评论" width="500px">
      <el-form :model="commentForm" label-width="70px">
        <el-form-item label="评分">
          <el-rate v-model="commentForm.rating" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
              type="textarea"
              v-model="commentForm.content"
              placeholder="请输入评论内容"
              :rows="4"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="commentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleComment()">提交</el-button>
      </template>
    </el-dialog>

    <el-container>
        <el-main style="position: relative;">
          <el-row style="width: 100%;">
            <el-col :span="12" style="display: flex; flex-direction: column; justify-content: center;">
              <el-row>
                <el-text class="custom-text" type="success">欢迎使用</el-text>
              </el-row>
<!--              <el-divider style="font-size: 100px;"/>-->
              <el-row style="margin-top: 40px;">
                <span style=" padding-left: 200px;">
                  <div class="custom-time-display" >{{ formattedDateTime }}</div>
                </span>
              </el-row>
            </el-col>
            <el-col :span="12">
              <div class="button-layout">
                <el-row style="padding-bottom: 80px;">
                  <el-button class="custom-btn" :dark="isDark" @click="goToPersonalCenter()">
                    <el-icon style="font-size: 40px; padding-right: 10px"><Box/></el-icon>
                    <el-text style="font-size:30px">个人中心</el-text>
                  </el-button>
                  <el-button class="custom-btn" style="margin-left: 45px" :dark="isDark" @click="goToShippingManager()">
                    <el-icon style="font-size: 40px; padding-right: 10px">
                      <Position/>
                    </el-icon>
                    <el-text style="font-size:30px">寄件</el-text>
                  </el-button>
                </el-row>
                <el-row>
                  <el-button class="custom-btn" :dark="isDark" @click="Model()">
                    <el-icon style="font-size: 40px; padding-right: 10px">
                      <Search/>
                    </el-icon>
                    <el-text style="font-size:30px">查询</el-text>
                  </el-button>
                  <el-button class="custom-btn" style="margin-left: 45px" :dark="isDark" @click="PersonalPackage()">
                    <el-icon style="font-size: 40px; padding-right: 10px">
                      <User/>
                    </el-icon>
                    <el-text style="font-size:30px">我的包裹</el-text>
                  </el-button>
                </el-row>
              </div>
            </el-col>
          </el-row>
        </el-main>
      </el-container>
  </div>
  <RouterView/>
</template>

<script setup>
import {ref} from 'vue';
import {useRouter} from 'vue-router'
import {isDark} from '../composables/dark'

import {computed, onMounted, onBeforeUnmount} from 'vue'
import axios from "axios";
import {ElMessage} from "element-plus";

const user = JSON.parse(localStorage.getItem("code_user"));
const studentId = user.studentId;
const router = useRouter()
const baseUrl = 'http://localhost:8080' // 你的后端服务地址
const fullAvatarUrl = ref('')

const formattedDateTime = ref('')
const notices = ref([])
const commentDialogVisible = ref(false)
// 写评论的表单
const commentForm = ref({
  studentId:studentId,
  rating:'',
  content:''
})

function formatDateToChineseFull(date) {
  const days = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekDay = days[date.getDay()]
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  const second = String(date.getSeconds()).padStart(2, '0')
  return `${year}年${month}月${day}日 ${weekDay} ${hour}:${minute}:${second}`
}

let timer = null

// 获取公告数据
const fetchNotices = async () => {
  try {
    const response = await axios.get(baseUrl + `/notice/getNoticeList`)
    if (response.data.code === "200") {
      notices.value = response.data.data || []
    } else {
      ElMessage.error(response.data.msg || '获取公告失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，获取公告失败')
    console.error('获取公告失败:', error)
  }
}

const fetchAvatar = async () => {
  const user = JSON.parse(localStorage.getItem("code_user"))// 从本地取出登录时保存的
  const studentId = user.studentId;
  console.log("保存的学号：",studentId)
  try {
    const userResponse = await axios.get(baseUrl+'/user/getUser',
        {params:{studentId}})

    // 获取用户头像
    const avatarPath = userResponse.data.data.avatar
    fullAvatarUrl.value = baseUrl + avatarPath
  } catch (err) {
    console.error('获取头像失败:', err)
  }
}

// 提交评论
const handleComment = async () => {
  try {
    // 使用 URL 参数传递 courier_number
    const res = await axios.post(baseUrl + `/comment/addSystemComment`, commentForm.value);

    if (res.data.code === "200") {
      ElMessage.success('评论成功')
      commentDialogVisible.value = false
      commentForm.value.rating=''
      commentForm.value.content=''

    } else {
      ElMessage.error(res.data.msg || '评论失败')
    }
  } catch (error) {
    ElMessage.error('qq评论失败')
    console.error(error)
  }}


onMounted(async () => {
  const updateTime = () => {
    const now = new Date()
    formattedDateTime.value = formatDateToChineseFull(now)
  }
  updateTime()
  timer = setInterval(updateTime, 1000)

  await fetchAvatar()
  document.addEventListener('click', closeDropdown)

  await fetchNotices()

})

onBeforeUnmount(() => {
  clearInterval(timer)
  document.removeEventListener('click', closeDropdown)

})

const showDropdown = ref(false)
const avatarContainer = ref(null)

// 点击头像的下拉菜单

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const closeDropdown = (event) => {
  if (avatarContainer.value && !avatarContainer.value.contains(event.target)) {
    showDropdown.value = false
  }
}

const handleLogout = () => {
  // 这里添加退出登录逻辑
  router.push('/')
  showDropdown.value = false
}

// 跳转到寄件管理页面
const goToShippingManager = () => {
  router.push('/shippingmanager/ship')
}

// 跳转到包裹管理页面
const PersonalPackage = () => {
  router.push('/personalpackage/allpackages')
}
//跳转到个人中心界面
const goToPersonalCenter = () => {
  router.push('/personalcenter')
}
// 跳转到数字孪生交互界面
const Model = () => {
  router.push('/model')
}


// 当前时间
const currentDateTime = ref(new Date())

</script>

<style>
.common-layout {
  background: url('@/assets/css/imgs/shouye_background.jpg') center/cover no-repeat fixed;
  position: relative;
  min-height: 100vh;
  width: 100%;
}

.button-layout {
  padding-top: 140px;
  padding-left: 100px;
}
.custom-btn {
  width: 200px;
  height: 180px;
  transition: all 0.3s;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.3); /* 设置透明背景 */
  backdrop-filter: blur(8px); /* 添加模糊效果 */
  border: 1px solid rgba(255, 255, 255, 0.6); /* 半透明边框 */
  font-family: "SimSun", serif; /* 使用宋体 */
  color: #000;
}

.custom-btn:not(.is-disabled):hover {
  transform: translateY(-2px);
  background-color: rgba(255, 255, 255, 0.3);
}

.custom-btn:not(.is-disabled):active {
  transform: translateY(1px);
  background-color: rgba(173, 216, 230, 0.4); /* 鼠标点击时变成浅蓝色 */
}

.custom-text{
  font-size: 80px;
  color: rgba(64, 64, 64, 0.88);
  margin-left: 220px;
  margin-top: 140px;
  font-family: "SimSun", serif; /* 使用宋体 */
}

.custom-time-display {
  font-size: 35px;
  font-weight: 600;
  color: rgba(64, 64, 64, 0.88);
  background-color: transparent;
  font-family: "SimSun", serif; /* 使用宋体 */
  padding: 8px 16px;
  border-radius: 10px;
}

@keyframes scroll-left {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(-100%);
  }
}

.notice-wrapper {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  height: 40px;
  display: flex;
  align-items: center;
  overflow: hidden;
  padding-left: 40px;
  width: 500px; /* 可根据实际内容宽度调整 */
}

.avatar-wrapper {
  position: relative;
  padding-right: 20px;
}

.avatar-container {
  position: relative;
  cursor: pointer;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid #ccc;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: 50px;
  background-color: #fff;
  min-width: 160px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 2000;
  padding: 8px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  font-size: 14px;
  cursor: pointer;
  color: #333;
  transition: background-color 0.3s ease;
}

.dropdown-item:hover {
  background-color: #f2f6fc;
  color: #409EFF;
}

.dropdown-item .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

.dropdown-divider {
  margin: 4px 0;
}


</style>