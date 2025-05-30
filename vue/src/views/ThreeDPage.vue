<template>
    <div style="background: #204267FF">
      <header class="bg_header">
        <div class="header">
          <nav class="left">
            <div class="head-item" @click="library1">临潼图书馆</div>
            <div class="head-item" @click="library2">金花图书馆</div>
            <div class="head-item" @click="library3">座位管理</div>
            <div class="head-item" @click="library4">总理</div>
          </nav>
          <div class="center">
            <span class="head_title">西安工程大学图书馆数字孪生平台</span>
          </div>
          <nav class="right">
            <div class="head-item" @click="reset">reset</div>
            <div class="head-item" @click="nnnn">图书馆仓库</div>
            <div class="head-item" @click="nnnn">行政楼</div>
            <div id="labelsDiv"></div>
          </nav>
        </div>
      </header>
      <div class="show">
        <video id="myVideo" width="1920" height="1080" autoplay muted></video>
      </div>
    </div>
  </template>
  
  <script setup lang="ts";>
  import { onMounted } from 'vue'
  // 需要先通过 npm 安装 jmuxer
  import JMuxer from 'jmuxer'
  
  let wss = null
  let ws = null
  let jmuxer = null
  
  onMounted(() => {
    connectRenderSocket()
    connectControlSocket()
  })
  
  function connectRenderSocket() {
    ws = new WebSocket('ws://127.0.0.1:8882/')
    ws.binaryType = 'arraybuffer'
  // @ts-ignore
    jmuxer = new JMuxer({
      node: 'myVideo',
      mode: 'video',
      flushingTime: 0,
      fps: 60,
      debug: false,
      onError: function () {
        jmuxer.reset()
      }
    })
  
    ws.onopen = () => {
      console.log('视频流 WebSocket 连接成功')
      listenToMouseEvents()
    }
  
    ws.onmessage = (event) => {
      jmuxer.feed({ video: new Uint8Array(event.data) })
    }
  }
  
  function connectControlSocket() {
    wss = new WebSocket('ws://127.0.0.1:9658/')
    wss.onopen = () => console.log('控制 WebSocket 连接成功')
  }
  
  function library1() {
    wss?.send(JSON.stringify({ key: '图书馆1', value: '115415' }))
  }
  
  function library2() {
    wss?.send(JSON.stringify({ key: '图书馆2', value: '2025' }))
  }
  
  function library3() {
    wss?.send(JSON.stringify({ key: '图书馆3', value: '8848' }))
  }
  
  function library4() {
    // 示例保留
  }
  
  function reset() {
    wss?.send(JSON.stringify({ key: 'reset', value: 'reset' }))
  }
  
  function nnnn() {
    // 预留功能
  }
  
  function listenToMouseEvents() {
    document.addEventListener('contextmenu', event => event.preventDefault())
    document.addEventListener('mousedown', handleMouseEvent)
    document.addEventListener('mouseup', handleMouseEvent)
    document.addEventListener('wheel', handleMouseEvent)
    document.addEventListener('mousemove', handleMouseEvent)
    document.addEventListener('keydown', handleKeyboardEvent)
    document.addEventListener('keyup', handleKeyboardEvent)
    document.addEventListener('dblclick', handleMouseEvent)
  }
  
  function handleMouseEvent(event) {
    const data = {
      type: event.type,
      key: '',
      button: event.button,
      pageX: event.pageX,
      pageY: event.pageY - 108,
      deltaY: event.deltaY || ''
    }
    ws?.send(JSON.stringify(data))
  }
  
  function handleKeyboardEvent(event) {
    const data = {
      type: event.type,
      key: event.key,
      button: '',
      pageX: '',
      pageY: '',
      deltaY: ''
    }
    ws?.send(JSON.stringify(data))
  }
  </script>
  
  <style scoped>
  @import "@/assets/css/reset.css";
  @import "@/assets/css/index.css";
  
  /* 你可以根据 reset.css 和 index.css 的样式进行调整 */
  </style>
  