import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/ai-chat',
    name: 'AIChat',
    component: () => import('../views/AIChat.vue')
  },
  {
    path: '/damai-ai',
    name: 'DaMaiAI',
    component: () => import('../views/DaMaiAi.vue')
  },
  {
    path: '/damai-rag',
    name: 'DaMaiRag',
    component: () => import('../views/DaMaiRag.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 