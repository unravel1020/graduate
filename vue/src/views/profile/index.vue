<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" link @click="handleEdit">
                编辑
              </el-button>
            </div>
          </template>
          <div class="profile-info">
            <div class="info-item">
              <span class="label">用户名：</span>
              <span>{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span>{{ userInfo.email }}</span>
            </div>
            <div class="info-item">
              <span class="label">注册时间：</span>
              <span>{{ userInfo.createTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 借阅统计 -->
      <el-col :span="16">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>借阅统计</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-value">{{ stats.totalBorrowed }}</div>
                <div class="stat-label">总借阅数</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-value">{{ stats.currentBorrowed }}</div>
                <div class="stat-label">当前借阅</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-value">{{ stats.overdue }}</div>
                <div class="stat-label">逾期未还</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 借阅记录 -->
    <el-card class="records-card">
      <template #header>
        <div class="card-header">
          <span>借阅记录</span>
        </div>
      </template>
      <el-table :data="borrowRecords" style="width: 100%">
        <el-table-column prop="bookTitle" label="书名" min-width="200" />
        <el-table-column prop="borrowTime" label="借阅时间" width="180" />
        <el-table-column prop="returnTime" label="应还时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '借阅中'"
              type="primary"
              size="small"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑个人信息对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑个人信息"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            show-password
            placeholder="不修改请留空"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
            placeholder="不修改请留空"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCurrentUser } from '@/api/auth'
import { getBorrowRecords, returnBook } from '@/api/book'

// 用户信息
const userInfo = ref({
  username: '',
  email: '',
  createTime: ''
})

// 统计数据
const stats = ref({
  totalBorrowed: 0,
  currentBorrowed: 0,
  overdue: 0
})

// 借阅记录
const borrowRecords = ref([])

// 编辑表单
const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({
  email: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback()
  } else {
    if (form.confirmPassword !== '') {
      formRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback()
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  newPassword: [
    { validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ]
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getCurrentUser()
    userInfo.value = res.data
    form.email = res.data.email
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取借阅记录
const fetchBorrowRecords = async () => {
  try {
    const res = await getBorrowRecords()
    borrowRecords.value = res.data.list
    
    // 计算统计数据
    stats.value = {
      totalBorrowed: res.data.total,
      currentBorrowed: res.data.list.filter(record => record.status === '借阅中').length,
      overdue: res.data.list.filter(record => record.status === '逾期未还').length
    }
  } catch (error) {
    console.error('获取借阅记录失败:', error)
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case '借阅中':
      return 'primary'
    case '已归还':
      return 'success'
    case '逾期未还':
      return 'danger'
    default:
      return 'info'
  }
}

// 编辑个人信息
const handleEdit = () => {
  dialogVisible.value = true
}

// 保存个人信息
const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    // TODO: 调用更新用户信息API
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchUserInfo()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 归还图书
const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要归还《${row.bookTitle}》吗？`,
      '归还确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await returnBook(row.id)
    ElMessage.success('归还成功')
    fetchBorrowRecords()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('归还失败:', error)
    }
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchBorrowRecords()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card,
.stats-card,
.records-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  .info-item {
    margin-bottom: 15px;
    
    .label {
      font-weight: bold;
      margin-right: 10px;
      color: var(--text-color);
    }
  }
}

.stat-item {
  text-align: center;
  padding: 20px;
  background-color: var(--background-color);
  border-radius: 8px;
  
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: var(--text-color);
  }
  
  .stat-label {
    font-size: 14px;
    color: var(--text-color-secondary);
    margin-top: 5px;
  }
}
</style> 