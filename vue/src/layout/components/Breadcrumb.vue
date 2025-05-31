<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
      <span
        v-if="index === breadcrumbs.length - 1"
        class="no-redirect"
      >{{ item.meta.title }}</span>
      <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const breadcrumbs = ref([])

// 生成面包屑数据
const getBreadcrumbs = () => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  breadcrumbs.value = matched
}

// 处理面包屑点击
const handleLink = (item) => {
  router.push(item.path)
}

// 监听路由变化
watch(
  () => route.path,
  () => {
    getBreadcrumbs()
  },
  { immediate: true }
)
</script>

<style scoped>
.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 60px;
}

.no-redirect {
  color: #97a8be;
  cursor: text;
}
</style> 