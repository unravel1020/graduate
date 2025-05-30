<template>
  <div>
    <!-- 主内容区 -->
    <el-main class="el-main">
      <el-row>
        <el-space :size="55">
          <div style="padding-left: 40px">
            <!--左侧卡片发件人信息-->
            <el-card style="width: 600px;">
              <h3 style="padding-bottom: 20px">发件人信息</h3>
              <el-form :model="senderForm" label-width="auto" style="width: 530px">
                <el-form-item label="姓名">
                  <el-input v-model="senderForm.senderName"/>
                </el-form-item>
                <el-form-item label="联系电话">
                  <el-input v-model="senderForm.senderPhone"/>
                </el-form-item>
                <el-form-item label="省市区">
                  <el-cascader
                      v-model="senderForm.senderPartAddress"
                      :options="areaData"
                      :props="{ label: 'label', value: 'label', children: 'children' }"
                  />
                </el-form-item>
                <el-form-item label="详细地址">
                  <el-input v-model="senderForm.senderDetailAddress"/>
                </el-form-item>
                <el-form-item label="快递公司">
                  <el-input v-model="senderForm.deliveryCompany"/>
                </el-form-item>
              </el-form>
            </el-card>
          </div>
          <el-icon :size="50" color="grey">
            <Right/>
          </el-icon>
          <!--右侧卡片收件人信息-->
          <el-card style="width: 600px">
            <h3 style="padding-bottom: 20px">收件人信息</h3>
            <el-form :model="receiverForm" label-width="auto" style="width: 530px">
              <el-form-item label="姓名">
                <el-input v-model="receiverForm.receiverName"/>
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="receiverForm.receiverPhone"/>
              </el-form-item>
              <el-form-item label="省市区">
                <el-cascader
                    v-model="receiverForm.receiverPartAddress"
                    :options="areaData"
                    :props="{ label: 'label', value: 'label', children: 'children' }"
                />
              </el-form-item>
              <el-form-item label="详细地址">
                <el-input v-model="receiverForm.receiverDetailAddress"/>
              </el-form-item>
              <el-form-item label="快递公司">
                <el-input v-model="receiverForm.deliveryCompany"/>
              </el-form-item>
            </el-form>
          </el-card>
        </el-space>
      </el-row>
      <div style="padding-left: 40px;padding-top: 40px">
        <!--寄件方式卡片 -->
        <el-card style="width: 1360px;height: auto">
          <h1 :size="50">寄件方式</h1>
          <el-form>
            <el-form-item>
              <el-radio-group v-model="form.deliveryMethod" style="padding-top: 20px;padding-left: 20px" @change="calculatePrice">
                <el-radio label="预约上门取件" border size="large">预约上门取件</el-radio>
                <el-radio label="服务点自寄" border size="large">服务点自寄</el-radio>
              </el-radio-group>
            </el-form-item>

          <!-- 预约上门取件表单 -->
          <div v-if="form.deliveryMethod === '预约上门取件'" style="padding-top: 10px;padding-left: 20px">
            <h3 >上门取件信息</h3>
            <el-form-item label="预约上门时间" style="padding-top: 20px;height: 55px">
              <el-date-picker v-model="form.pickupTime" type="datetime" label="预约上门时间" placeholder="预约上门时间" />
            </el-form-item>
              <el-form-item label="寄件保障" style="padding-top: 10px;padding-right: 40px">
                <el-radio-group v-model="form.pickupProtection" @change="calculatePrice">
                  <el-radio label="2小时内上门(10元)"/>
                  <el-radio label="1小时内上门(5元)"/>
                </el-radio-group>
              </el-form-item>
          </div>

          <!-- 服务点自寄表单 -->
          <div v-else-if="form.deliveryMethod === '服务点自寄'" style="padding-top: 10px;padding-left: 20px">
            <h3 >服务点自寄信息</h3>
            <el-form-item label="请选择服务点" style="padding-top: 20px">
              <el-select v-model="form.servicePoint" placeholder="请选择服务点">
                <el-option label="菜鸟驿站A" value="A"/>
                <el-option label="菜鸟驿站B" value="B"/>
              </el-select>
            </el-form-item>
            <el-form-item label="预计到达时间" style="padding-top: 20px">
              <el-date-picker v-model="form.estimatedArrivalTime" type="datetime" placeholder="预计到达时间" />
            </el-form-item>
          </div>
          </el-form>
        </el-card>
      </div>
      <!--物品信息-->
      <div style="padding-left: 40px;padding-top: 40px">
        <el-card style="width: 1360px;">
          <h3>物品信息</h3>
          <el-form :model="typeForm" label-width="auto" style="width: 530px; padding-top: 20px;padding-left: 20px">
            <el-form-item label="物品种类">
              <el-select
                  v-model="typeForm.itemType"
                  placeholder="请选择"
                  style="width: 530px; height: 40px"
              >
                <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="设置重量">
              <el-input-number v-model="typeForm.weight" :min="1" :max="10" @change="calculatePrice"/>
            </el-form-item>
            <el-form-item label="备注">
              <el-input
                  v-model="typeForm.remark"
                  style="width: 240px"
                  autosize
                  type="textarea"
                  placeholder="Please input"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </div>
      <!--付款方式-->
      <div style="padding-left: 40px; padding-top: 40px">
        <el-card style="width: 1360px;">
          <h3>付款方式</h3>
          <el-radio-group v-model="payment" style="padding-top: 20px;padding-left: 20px">
            <el-radio value="1" size="large" border>寄付现结</el-radio>
            <el-radio value="2" size="large" border>到 付</el-radio>
          </el-radio-group>
        </el-card>
        <div style="padding-top: 20px;width: 1360px;">
          <el-card style="width: 1360px;">
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <!-- 左边显示价格 -->
              <div>
                预计总费用：
                <span style="color: #409EFF">{{ totalPrice }} 元</span>
              </div>
              <!-- 右边显示提交按钮 -->
              <el-button style="width: 100px" type="primary" plain @click="fetchShip">
                提交
              </el-button>
            </div>
          </el-card>

        </div>
      </div>
      <router-view/>
    </el-main>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {Box, SwitchButton, Right} from '@element-plus/icons-vue'
import {regionData} from 'element-china-area-data';
import axios from "axios";
import {ElMessage} from "element-plus"; // 导入数据

const baseUrl = 'http://localhost:8080' // 你的后端服务地址

// 发件人信息表单
const senderForm = ref({
  senderName: '',
  senderPhone: '',
  senderPartAddress: '',
  senderDetailAddress: '',
  deliveryCompany: '',
})

// 收件人信息表单
const receiverForm = ref({
  receiverName: '',
  receiverPhone: '',
  receiverPartAddress: '',
  receiverDetailAddress: '',
  deliveryCompany: '',
})

// 物品信息表单
const typeForm = ref({
  itemType: '',
  weight: '',
  remark: '',
})

// 寄件方式
const form = ref({
      deliveryMethod: '',
      pickupTime: '',
      pickupProtection: '',
      servicePoint: '',
      estimatedArrivalTime: '',
    })

const value = ref('')

// 物品种类
const options = [
  {value: '日用品', label: '日用品',},
  {value: '食物', label: '食物',},
  {value: '文件', label: '文件',},
  {value: '衣物', label: '衣物',},
  {value: '数码设备', label: '数码设备',},
  {value: '生鲜', label: '生鲜',},
  {value: '易碎品', label: '易碎品',},
  {value: '其他', label: '其他',},
]

// 付款方式
const payment = ref('')

const areaData = JSON.parse(JSON.stringify(regionData)) // 深拷贝一份，避免污染原数据

const totalPrice = ref(0)

//获取寄件信息
const fetchShip = async () => {
  calculatePrice(); // 确保提交时也重新计算
  const user = JSON.parse(localStorage.getItem("code_user"));
  const studentId = user.studentId;

  // 拆分级联地址
  const [senderprovince = '', sendercity = '', senderdistrict = ''] = senderForm.value.senderPartAddress || [];
  const [receiverprovince = '', receivercity = '', receiverdistrict = ''] = receiverForm.value.receiverPartAddress || [];

  // ✅ 拼接完整地址
  const senderAddress = `${senderprovince}${sendercity}${senderdistrict}${senderForm.value.senderDetailAddress}`;
  const receiverAddress = `${receiverprovince}${receivercity}${receiverdistrict}${receiverForm.value.receiverDetailAddress}`;

  // 构造完整的数据
  const shipData = {
    studentId,
    senderName: senderForm.value.senderName,
    senderPhone: senderForm.value.senderPhone,
    senderprovince,
    sendercity,
    senderdistrict,
    senderDetailAddress: senderForm.value.senderDetailAddress,
    receiverName: receiverForm.value.receiverName,
    receiverPhone: receiverForm.value.receiverPhone,
    receiverprovince,
    receivercity,
    receiverdistrict,
    receiverDetailAddress: receiverForm.value.receiverDetailAddress,

    // ✅ 加入拼接后的完整地址字段
    senderAddress,
    receiverAddress,

    deliveryCompany: senderForm.value.deliveryCompany,
    deliveryMethod: form.value.deliveryMethod,
    pickupTime: form.value.pickupTime
        ? new Date(form.value.pickupTime).toISOString()
        : null,
    pickupProtection: form.value.pickupProtection,
    servicePoint: form.value.servicePoint,
    estimatedArrivalTime: form.value.estimatedArrivalTime
        ? new Date(form.value.estimatedArrivalTime).toISOString()
        : null,
    itemType: typeForm.value.itemType,
    weight: typeForm.value.weight,
    remark: typeForm.value.remark,
    payment: payment.value,
  };

  console.log("pickupTime:", form.value.pickupTime);
  console.log("shipData:", shipData);

  try {
    const res = await axios.post(baseUrl + '/ship/saveShip', shipData);
    if (res.data.code === "200") {
      ElMessage.success('保存成功');
    } else {
      ElMessage.error(res.data.msg);
    }
  } catch (error) {
    ElMessage.error('保存失败，请稍后再试');
    console.error('请求错误:', error);
  }
};


// 计算价钱
const calculatePrice = () => {
  let price = 10; // 基础价
  const weight = Number(typeForm.value.weight) || 0;
  price += Math.max(0, weight - 1) * 5; // 从第2kg开始加价

  if (form.value.deliveryMethod === '预约上门取件') {
    if (form.value.pickupProtection === '2小时内上门(10元)') {
      price += 10;
    } else if (form.value.pickupProtection === '1小时内上门(5元)') {
      price += 5;
    }
  }

  totalPrice.value = price;
};

</script>

<style scoped>
.custom-footer {
  width: 100%;
  height: 60px;
  line-height: 60px; /* 垂直居中 */
  text-align: center; /* 水平居中 */
  background-color: #0c2c53; /* 背景色 */
  color: #666; /* 文字颜色 */
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}
</style>