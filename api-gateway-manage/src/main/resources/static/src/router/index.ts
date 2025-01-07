import {createRouter, createWebHashHistory} from 'vue-router'

const routes = [
  {
    path: '',
    name: '路由列表',
    component: () => import('@/views/route/RouteList.vue'),
    meta: {
      keepAlive: true // 需要缓存
    }
  },
  {
    path: '/detail',
    name: '路由详情',
    component: () => import('@/views/route/RouteInfo.vue'),
  }
]

const router = createRouter({
  // history: createWebHistory(process.env.BASE_URL),
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

export default router
