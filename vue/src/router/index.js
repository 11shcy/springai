import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/ai-chat',
    name: 'ChatPro',
    component: () => import('../views/ChatPro.vue')
  },
  {
    path: '/damai-ai',
    name: 'DaMaiAI',
    component: () => import('../views/DaMaiAi.vue')
  },
  {
    path: '/damai-rag',
    name: 'SmartRag',
    component: () => import('../views/SmartRag.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 