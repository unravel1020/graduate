<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>图书馆管理系统</h2>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    console.log('Attempting login with:', form)
    const res = await login(form)
    console.log('Login response:', res)
    
    if (res.data && res.data.data && res.data.data.token) {
      localStorage.setItem('token', res.data.data.token)
      ElMessage.success('登录成功')
      router.push('/books')
    } else {
      ElMessage.error('登录失败：未获取到token')
    }
  } catch (error) {
    console.error('登录失败:', error)
    if (error.response) {
      // 服务器返回错误响应
      const status = error.response.status
      const message = error.response.data?.message || '登录失败'
      
      if (status === 403) {
        ElMessage.error('用户名或密码错误')
      } else {
        ElMessage.error(message)
      }
    } else if (error.request) {
      // 请求发送失败
      ElMessage.error('无法连接到服务器，请检查网络连接')
    } else {
      // 其他错误
      ElMessage.error(error.message || '登录失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
  
  .card-header {
    text-align: center;
    
    h2 {
      margin: 0;
      color: var(--el-color-primary);
    }
  }
}

.login-button {
  width: 100%;
}
</style> 