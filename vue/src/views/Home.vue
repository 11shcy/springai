<template>
  <div class="home" :class="{ 'dark': isDark }">
    <div class="container">
      <h1 class="title">具体 AI 应用</h1>
      <div class="cards-grid">
        <router-link 
          v-for="app in aiApps" 
          :key="app.id"
          :to="app.route"
          class="card"
        >
          <div class="card-content">
            <component :is="app.icon" class="icon" />
            <h2>{{ app.title }}</h2>
            <p>{{ app.description }}</p>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useDark } from '@vueuse/core'
import { 
  ChatBubbleLeftRightIcon,
  HeartIcon,
  UserGroupIcon,
  DocumentTextIcon
} from '@heroicons/vue/24/outline'
import DamaiAssistantIcon from '../components/icons/DamaiAssistantIcon.vue'

const isDark = useDark()

const aiApps = ref([
  {
    id: 1,
    title: '简单聊天',
    description: '与 AI 聊天玩儿',
    route: '/ai-chat',
    icon: ChatBubbleLeftRightIcon
  },
  {
    id: 2,
    title: '大麦贴心助手',
    description: '每天陪伴你，解决大麦项目的问题',
    route: '/damai-ai',
    icon: DamaiAssistantIcon
  },
  {
    id: 3,
    title: '大麦规则助手',
    description: '帮你解决大麦相关规则问题',
    route: '/damai-rag',
    icon: DamaiAssistantIcon
  }
])
</script>

<style scoped lang="scss">
.home {
  min-height: 100vh;
  padding: 2rem;
  background: var(--bg-color);
  transition: background-color 0.3s;

  .container {
    max-width: 1600px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  .title {
    text-align: center;
    font-size: 2.5rem;
    margin-bottom: 3rem;
    background: linear-gradient(45deg, rgba(255, 55, 29, 0.85), #ff8f29);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    animation: fadeIn 1s ease-out;
  }

  .cards-grid {
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    gap: 2rem;
    justify-items: center;
    padding: 1rem;
    max-width: 400px;
    margin: 0 auto;

    @media (min-width: 1200px) {
      grid-template-columns: repeat(3, 1fr);
      max-width: 1200px;
    }
  }

  .card {
    position: relative;
    width: 100%;
    max-width: 320px;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 2rem;
    text-decoration: none;
    color: inherit;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    border: 1px solid rgba(255, 255, 255, 0.1);
    overflow: hidden;
    animation: cardAppear 0.6s ease-out forwards;
    opacity: 0;
    transform: translateY(20px);

    @for $i from 1 through 3 {
      &:nth-child(#{$i}) {
        animation-delay: #{$i * 0.2}s;
      }
    }

    .dark & {
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.05);
    }

    &:hover {
      transform: translateY(-8px) scale(1.02);
      box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
      
      .dark & {
        box-shadow: 0 15px 30px rgba(0, 0, 0, 0.4);
      }

      .icon {
        transform: scale(1.1) rotate(5deg);
      }
    }

    .card-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
    }

    .icon {
      width: 48px;
      height: 48px;
      margin-bottom: 1rem;
      color: rgba(255, 55, 29, 0.85);
      transition: transform 0.3s ease;

      &.heart-icon {
        color: rgba(255, 55, 29, 0.85);
        animation: pulse 1.5s ease-in-out infinite;
      }
    }

    h2 {
      font-size: 1.5rem;
      margin-bottom: 0.5rem;
      transition: color 0.3s ease;
    }

    p {
      color: #666;
      font-size: 1rem;
      transition: color 0.3s ease;

      .dark & {
        color: #999;
      }
    }
  }

  &.dark {
    background: #1a1a1a;
    
    .card {
      background: rgba(255, 255, 255, 0.05);
      
      p {
        color: #999;
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes cardAppear {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .home {
    padding: 1rem;
    
    .container {
      padding: 0 1rem;
    }
    
    .title {
      font-size: 2rem;
    }

    .card {
      max-width: 100%;
    }
  }
}
</style> 