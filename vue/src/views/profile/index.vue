<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="showPasswordDialog = true">修改密码</el-button>
            </div>
          </template>
          <div class="user-info">
            <el-avatar :size="64" :src="userInfo?.avatar || defaultAvatar" />
            <div class="info">
              <h3>{{ userInfo?.username || '未登录' }}</h3>
              <p>角色：{{ userInfo?.role === 'admin' ? '管理员' : '普通用户' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 我的预约卡片 -->
      <el-col :span="16">
        <el-card class="reservation-card">
          <template #header>
            <div class="card-header">
              <span>我的预约</span>
              <el-button type="primary" @click="showReservationDialog = true">查看预约</el-button>
            </div>
          </template>
          <div class="reservation-list">
            <el-table :data="reservationList" style="width: 100%">
              <el-table-column prop="seatName" label="座位" />
              <el-table-column prop="startTime" label="开始时间" />
              <el-table-column prop="endTime" label="结束时间" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                    {{ row.status === 'active' ? '进行中' : '已完成' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button
                    v-if="row.status === 'active'"
                    type="danger"
                    size="small"
                    @click="handleCancelReservation(row)"
                  >
                    取消预约
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="400px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" @click="handleChangePassword">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预约详情对话框 -->
    <el-dialog
      v-model="showReservationDialog"
      title="预约详情"
      width="800px"
    >
      <el-table :data="reservationList" style="width: 100%">
        <el-table-column prop="seatName" label="座位" />
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '进行中' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'active'"
              type="danger"
              size="small"
              @click="handleCancelReservation(row)"
            >
              取消预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { auth } from '@/utils/api'
import { seats } from '@/utils/api'

// 用户信息
const userInfo = ref({
  username: '',
  role: '',
  avatar: ''
})
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 预约列表
const reservationList = ref([])

// 对话框显示状态
const showPasswordDialog = ref(false)
const showReservationDialog = ref(false)

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await auth.getProfile()
    if (res.data) {
      userInfo.value = res.data
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取预约列表
const fetchReservations = async () => {
  try {
    const res = await seats.getReservations()
    reservationList.value = res.data
  } catch (error) {
    console.error('获取预约列表失败:', error)
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    await auth.changePassword(passwordForm.value)
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  }
}

// 取消预约
const handleCancelReservation = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await seats.cancelReservation(row.id)
    ElMessage.success('预约取消成功')
    fetchReservations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error)
    }
  }
}

// 初始化
onMounted(() => {
  fetchUserInfo()
  fetchReservations()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card,
.reservation-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
}

.info p {
  margin: 0;
  color: #666;
}

.reservation-list {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 