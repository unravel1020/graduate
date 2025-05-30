<template>
  <div style="padding-left: 40px; padding-top: 20px;">
    <el-input placeholder="请输入运单号查询" style="width: 300px" v-model="trackingNumber"></el-input>
    <el-button type="info" plain style="margin-left: 10px" @click="searchShip">查询</el-button>
    <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
  </div>

  <div style="padding-top: 40px;padding-left: 20px;">
    <el-row
        v-for="(item, index) in formList"
        :key="index"
        justify="center"
        align="middle"
        class="row-space"
        :gutter="20"
    >
      <el-card class="card-style" style="width: 90%">
        <div style="padding-bottom: 10px"><strong>运单号：</strong> {{ item.trackingNumber || '暂无' }}</div>
        <el-row type="flex" align="middle" style="width: 100%">
          <!-- 发件人卡片 -->
          <el-col :span="9">
            <div style="padding-bottom: 10px"><strong>发件人姓名：</strong> {{ item.senderName || '暂无' }}</div>
            <div style="padding-bottom: 10px"><strong>发件人联系方式：</strong> {{ item.senderPhone || '暂无' }}</div>
            <div style="padding-bottom: 10px"><strong>发件人地址：</strong> {{ item.senderAddress || '暂无地址' }}</div>
          </el-col>

          <!-- 中间箭头 -->
          <el-col :span="1" class="arrow-col">
            <el-icon size="30"><Right /></el-icon>
          </el-col>

          <!-- 收件人卡片 -->
          <el-col :span="9">
            <div style="padding-bottom: 10px"><strong>收件人姓名：</strong> {{ item.receiverName || '暂无' }}</div>
            <div style="padding-bottom: 10px"><strong>收件人联系方式：</strong> {{ item.receiverPhone || '暂无' }}</div>
            <div style="padding-bottom: 10px"><strong>收件人地址：</strong> {{ item.receiverAddress || '暂无地址' }}</div>
          </el-col>

          <!-- 操作按钮 -->
          <el-col :span="3">
            <el-button type="danger" plain @click="openDeleteDialog(item)">删除</el-button>
          </el-col>
        </el-row>
      </el-card>
    </el-row>
    <!--确认删除对话框-->
    <el-dialog v-model="deleteDialogVisible" width="500">
      <span>确认删除吗？</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleDelete">确认删除</el-button>
        </div>
      </template>
    </el-dialog>


  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const baseUrl = 'http://localhost:8080'
const formList = ref([])             // 记录列表
const deleteDialogVisible = ref(false) // 控制删除对话框显隐
const selectedRow = ref(null)          // 存储要删除的记录
const trackingNumber = ref()

// 获取运单数据
const fetchShip = async () => {
  const user = JSON.parse(localStorage.getItem("code_user"))
  const studentId = user.studentId
  try {
    const res = await axios.get(baseUrl + '/ship/getShipList', {
      params: { studentId }
    })
    formList.value = res.data.data || []
  } catch (err) {
    console.error('获取失败:', err)
  }
}

// 点击删除按钮，打开对话框并记录当前 row
const openDeleteDialog = (row) => {
  selectedRow.value = row
  deleteDialogVisible.value = true
}

// 确认删除
const handleDelete = async () => {
  const row = selectedRow.value
  if (!row) {
    ElMessage.error('未选择要删除的记录')
    return
  }

  try {
    const res = await axios.delete(baseUrl + `/ship/deleteShip`, {
      params: { shipmentId: row.shipmentId }
    })

    if (res.data.code === "200") {
      ElMessage.success('删除成功')
      deleteDialogVisible.value = false
      await fetchShip()
    } else {
      ElMessage.error(res.data.msg || '删除失败')
    }
  } catch (error) {
    ElMessage.error('删除运单失败')
    console.error(error)
  }
}

const searchShip = async () => {
  if (!trackingNumber.value) {
    ElMessage.warning('请输入运单号')
    return
  }

  try {
    const response = await axios.get(baseUrl + '/ship/getShipByTrackingNumber', {
      params: { trackingNumber: trackingNumber.value }
    })

    const result = response.data.data
    console.log("查询结果：", result)

    if (!result || result.length === 0) {
      ElMessage.info('没有查到相关快递记录')
      formList.value = []
    } else {
      formList.value = result  // 注意：这里是个数组，直接赋值给 formList
    }

  } catch (error) {
    ElMessage.error('查询失败，请稍后再试')
    console.error(error)
  }
}

// 重置按钮
// 重置按钮  ok
const reset = async () => {
  await fetchShip()
  trackingNumber.value = ''
}

onMounted(async () => {
  await fetchShip()
})

</script>

<style scoped>
.row-space {
  margin-bottom: 30px;
}
.card-style {
  padding: 30px;
}
.arrow-col {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>