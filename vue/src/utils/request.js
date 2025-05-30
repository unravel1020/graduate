import axios from "axios";
import {ElMessage} from "element-plus";
import router from '@/router'

// 创建axios实例
const service = axios.create({
    baseURL: 'http://localhost:8080', // 直接指定后端地址
    timeout: 15000, // 增加超时时间
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
})

// 请求拦截器
//可以自请求发送前对请求做一些处理
service.interceptors.request.use(config =>{
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
        // 将token添加到请求头
        config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 添加调试日志
    console.log('Request URL:', `${config.baseURL}${config.url}`)
    console.log('Request Method:', config.method)
    console.log('Request Headers:', config.headers)
    console.log('Request Data:', config.data)
    
    return config
},error => {
    console.error('Request Error:', error)
    return Promise.reject(error)
});

// 响应拦截器
//可以在接口响应后统一处理结果
service.interceptors.response.use(
    response => {
        const res = response.data;
        //兼容服务端返回的字符串数据
        if(typeof  res === 'string') {
            res = res ? JSON.parse(res):res
        }
        
        // 添加调试日志
        console.log('Response Status:', response.status)
        console.log('Response Headers:', response.headers)
        console.log('Response Data:', res)
        
        // 如果返回的状态码不是200，说明接口请求有误
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            
            // 401: 未登录或token过期
            if (res.code === 401) {
                // 清除本地token
                localStorage.removeItem('token')
                localStorage.removeItem('username')
                // 跳转到登录页
                router.push('/login')
            }
            
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        return res;
    },
    error => {
        // 添加详细的错误日志
        console.error('Response Error:', {
            status: error.response?.status,
            statusText: error.response?.statusText,
            data: error.response?.data,
            config: error.config,
            message: error.message,
            code: error.code
        })
        
        if (error.code === 'ECONNABORTED') {
            ElMessage.error('请求超时，请检查网络连接')
        } else if (error.code === 'ERR_NETWORK') {
            ElMessage.error('网络错误，请确保后端服务器正在运行')
        } else if (error.response?.status === 403) {
            ElMessage.error('没有权限访问该资源')
        } else {
            ElMessage.error(error.message || '请求失败')
        }
        return Promise.reject(error)
    }
)

export default service