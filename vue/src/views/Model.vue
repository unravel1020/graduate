<template>
  <div style="background: #204267FF; padding: 10px;">
    <header class="bg_header" style="background: #003366; padding: 10px; display: flex; justify-content: space-between; align-items: center;">
      <el-button @click="goToWarehouseA">快递站仓库A</el-button>
      <el-button @click="resetView">重置</el-button>
      <div style="color: white; font-size: 32px; font-family: 'SimSun', serif;">
        校园快递站数字孪生平台
      </div>
      <el-button @click="goToWarehouseB">快递站仓库B</el-button>
      <el-button @click="goToWarehouseC">快递站仓库C</el-button>
    </header>

    <div style="position: relative; margin-top: 10px; display: flex; justify-content: center;">
      <video id="myVideo" width="960" height="540" autoplay muted></video>
      <!-- 在视频上方用绝对定位插入提示标 -->
      <div v-for="marker in markers"
           :key="marker.id"
           class="hint-marker"
           :style="marker.style"
           @click="onMarkerClick(marker)">
        {{ marker.count }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, reactive } from 'vue'
import JMuxer from 'jmuxer'

let wsVideo = null
let wsControl = null
let jmuxer = null

const WS_VIDEO_URL   = 'ws://127.0.0.1:8882/'
const WS_CONTROL_URL = 'ws://127.0.0.1:9658/'

// Reactive 数组，存放所有提示标信息
const markers = reactive([])

// 初始化几个示例标
function initMarkers() {
  // 你可以改为从后端接口拉取真实数据
  const demoData = [
    { id: 'pkg1', x: 200, y: 150, count: 3 },
    { id: 'pkg2', x: 600, y: 300, count: 1 },
  ]
  demoData.forEach(item => {
    markers.push({
      id:     item.id,
      count:  item.count,
      style: {
        position:   'absolute',
        top:        item.y + 'px',
        left:       item.x + 'px',
        background: 'rgba(255,0,0,0.7)',
        color:      '#fff',
        padding:    '4px 8px',
        borderRadius: '8px',
        cursor:     'pointer',
        userSelect: 'none',
      }
    })
  })
}

// 点击提示标—跳转视点
function onMarkerClick(marker) {
  console.log('点击了提示标', marker.id)
  // 调用三维引擎接口切换视点
  if (window.W3DEngShell && typeof window.W3DEngShell.CallMethod === 'function') {
    // 假设插件方法：JumpToView(viewName)
    window.W3DEngShell.CallMethod('JumpToView', marker.id)
  }
  // 同时给后端/管理后台发一个查询命令
  if (wsControl && wsControl.readyState === WebSocket.OPEN) {
    const queryMsg = { action: 'queryPackage', packageId: marker.id }
    wsControl.send(JSON.stringify(queryMsg))
    console.log('下发查询命令:', queryMsg)
  }
}

// 切换仓库视角
function goToWarehouseA() { sendControl({ key:'快递站仓库A' }) }
function goToWarehouseB() { sendControl({ key:'快递站仓库B' }) }
function goToWarehouseC() { sendControl({ key:'快递站仓库C' }) }
function resetView()          { sendControl({ key:'reset' }) }

// 统一通过控制 WS 发消息
function sendControl(msg) {
  if (wsControl && wsControl.readyState === WebSocket.OPEN) {
    wsControl.send(JSON.stringify(msg))
    console.log('发送控制指令:', msg)
  }
}

onMounted(() => {
  initMarkers()

  jmuxer = new JMuxer({ node:'myVideo', mode:'video', fps:60 })
  wsVideo = new WebSocket(WS_VIDEO_URL)
  wsVideo.binaryType = 'arraybuffer'
  wsVideo.onmessage = e => jmuxer.feed({ video: new Uint8Array(e.data) })

  wsControl = new WebSocket(WS_CONTROL_URL)

  // 插件交互：收到三维程序消息时也可以处理
  wsControl.onmessage = e => {
    console.log('收到三维程序消息:', e.data)
  }
})

onBeforeUnmount(() => {
  wsVideo?.close()
  wsControl?.close()
})
</script>

<style scoped>
.el-button {
  background-color: transparent;
  border: none;
  color: #fff;
  font-family: "SimSun", serif;
  font-size: 20px;
  padding: 10px 20px;
}
.el-button:hover { color: #aaccff; }

.hint-marker {
  transition: transform .2s;
}
.hint-marker:hover {
  transform: scale(1.2);
}
</style>
