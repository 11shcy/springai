<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useDark, useToggle } from '@vueuse/core'
import { SunIcon, MoonIcon } from '@heroicons/vue/24/outline'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { ref } from 'vue'

const isDark = useDark()
const toggleDark = useToggle(isDark)
const router = useRouter()

// 添加全局状态来跟踪当前路由
const currentRoute = ref(router.currentRoute.value.path)

// 添加全局路由守卫
router.beforeEach((to, from, next) => {
  // 如果是从 ChatPDF 页面离开
  if (from.path === '/chat-pdf') {
    // 触发一个自定义事件，让 ChatPDF 组件知道要清理资源
    window.dispatchEvent(new CustomEvent('cleanupChatPDF'))
  }
  currentRoute.value = to.path
  next()
})
</script>

<template>
  <div class="app" :class="{ 'dark': isDark }">
    <nav class="navbar">
      <router-link to="/" class="logo">大麦 AI</router-link>
      <button @click="toggleDark()" class="theme-toggle">
        <SunIcon v-if="isDark" class="icon" />
        <MoonIcon v-else class="icon" />
      </button>
    </nav>
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<style lang="scss">
:root {
  --bg-color: #f5f5f5;
  --text-color: #333;
}

.dark {
  --bg-color: #1a1a1a;
  --text-color: #fff;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen,
    Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  color: var(--text-color);
  background: var(--bg-color);
  min-height: 100vh;
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  margin-top: 18px;
  margin-left: auto;
  margin-right: auto;
  width: 90vw;
  max-width: 1200px;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
  height: 60px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid #f0f0f0;
}

.navbar .logo {
  font-size: 1.5rem;
  font-weight: 800;
  letter-spacing: 2px;
  color: #ff3b1d;
  background: none;
  -webkit-text-fill-color: unset;
  text-shadow: 0 2px 8px rgba(255,59,29,0.08);
  padding: 0;
  margin: 0;
  line-height: 60px;
}

.theme-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  transition: background-color 0.3s;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }

  .icon {
    width: 24px;
    height: 24px;
    color: var(--text-color);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .navbar {
    padding: 1rem;
  }
}
</style>
