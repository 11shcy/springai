<template>
  <div class="ai-companion" :class="{ 'dark': isDark }">
    <div class="chat-container">
      <div class="sidebar">
        <div class="history-header">
          <h2>对话历史</h2>
          <button class="new-chat" @click="initiateNewSession">
            <PlusIcon class="icon" />
            新会话
          </button>
        </div>
        <div class="history-list">
          <div 
            v-for="session in sessionHistory" 
            :key="session.id"
            class="history-item"
            :class="{ 'active': activeSessionId === session.id }"
            @click="switchSession(session.id)"
          >
            <ChatBubbleLeftRightIcon class="icon" />
            <span class="title">{{ session.title || '新会话' }}</span>
          </div>
        </div>
      </div>
      
      <div class="chat-main">
        <div class="messages" ref="messagesContainer">
          <ChatMessage
            v-for="(message, index) in activeMessages"
            :key="index"
            :message="message"
            :is-stream="isProcessing && index === activeMessages.length - 1"
          />
        </div>
        
        <div class="input-area">
          <div v-if="attachedFiles.length > 0" class="selected-files">
            <div v-for="(file, index) in attachedFiles" :key="index" class="file-item">
              <div class="file-info">
                <DocumentIcon class="icon" />
                <span class="file-name">{{ file.name }}</span>
                <span class="file-size">({{ formatFileSize(file.size) }})</span>
              </div>
              <button class="remove-btn" @click="detachFile(index)">
                <XMarkIcon class="icon" />
              </button>
            </div>
          </div>

          <div class="input-row">
            <div class="file-upload">
              <input 
                type="file" 
                ref="fileSelector"
                @change="handleFileSelection"
                accept="image/*,audio/*,video/*"
                multiple
                class="hidden"
              >
              <button 
                class="upload-btn"
                @click="openFileSelector"
                :disabled="isProcessing"
              >
                <PaperClipIcon class="icon" />
              </button>
            </div>

            <textarea
              v-model="userQuery"
              @keydown.enter.prevent="submitMessage"
              :placeholder="getInputPlaceholder()"
              rows="1"
              ref="queryInput"
            ></textarea>
            <button 
              class="send-button" 
              @click="submitMessage"
              :disabled="isProcessing || (!userQuery.trim() && !attachedFiles.length)"
            >
              <PaperAirplaneIcon class="icon" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useDark } from '@vueuse/core'
import { 
  ChatBubbleLeftRightIcon, 
  PaperAirplaneIcon,
  PlusIcon,
  PaperClipIcon,
  DocumentIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'

const isDark = useDark()
const messagesContainer = ref(null)
const queryInput = ref(null)
const userQuery = ref('')
const isProcessing = ref(false)
const activeSessionId = ref(null)
const activeMessages = ref([])
const sessionHistory = ref([])
const fileSelector = ref(null)
const attachedFiles = ref([])

// 自动调整输入框高度
const adjustTextareaHeight = () => {
  const textarea = queryInput.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  }else{
    textarea.style.height = '50px'
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 文件类型限制
const FILE_RESTRICTIONS = {
  image: { 
    maxSize: 10 * 1024 * 1024,  // 单个文件 10MB
    maxFiles: 3,                 // 最多 3 个文件
    description: '图片文件'
  },
  audio: { 
    maxSize: 10 * 1024 * 1024,  // 单个文件 10MB
    maxDuration: 180,           // 3分钟
    maxFiles: 3,                // 最多 3 个文件
    description: '音频文件'
  },
  video: { 
    maxSize: 150 * 1024 * 1024, // 单个文件 150MB
    maxDuration: 40,            // 40秒
    maxFiles: 3,                // 最多 3 个文件
    description: '视频文件'
  }
}

// 触发文件选择
const openFileSelector = () => {
  fileSelector.value?.click()
}

// 检查文件是否符合要求
const validateFile = async (file) => {
  const type = file.type.split('/')[0]
  const restriction = FILE_RESTRICTIONS[type]
  
  if (!restriction) {
    return { valid: false, error: '不支持的文件类型' }
  }
  
  if (file.size > restriction.maxSize) {
    return { valid: false, error: `文件大小不能超过${restriction.maxSize / 1024 / 1024}MB` }
  }
  
  if ((type === 'audio' || type === 'video') && restriction.maxDuration) {
    try {
      const duration = await getMediaDuration(file)
      if (duration > restriction.maxDuration) {
        return { 
          valid: false, 
          error: `${type === 'audio' ? '音频' : '视频'}时长不能超过${restriction.maxDuration}秒`
        }
      }
    } catch (error) {
      return { valid: false, error: '无法读取媒体文件时长' }
    }
  }
  
  return { valid: true }
}

// 获取媒体文件时长
const getMediaDuration = (file) => {
  return new Promise((resolve, reject) => {
    const element = file.type.startsWith('audio/') ? new Audio() : document.createElement('video')
    element.preload = 'metadata'
    
    element.onloadedmetadata = () => {
      resolve(element.duration)
      URL.revokeObjectURL(element.src)
    }
    
    element.onerror = () => {
      reject(new Error('无法读取媒体文件'))
      URL.revokeObjectURL(element.src)
    }
    
    element.src = URL.createObjectURL(file)
  })
}

// 修改文件上传处理函数
const handleFileSelection = async (event) => {
  const files = Array.from(event.target.files || [])
  if (!files.length) return
  
  // 检查所有文件类型是否一致
  const firstFileType = files[0].type.split('/')[0]
  const hasInconsistentType = files.some(file => file.type.split('/')[0] !== firstFileType)
  
  if (hasInconsistentType) {
    alert('请选择相同类型的文件（图片、音频或视频）')
    event.target.value = ''
    return
  }

  // 验证所有文件
  for (const file of files) {
    const { valid, error } = await validateFile(file)
    if (!valid) {
      alert(error)
      event.target.value = ''
      attachedFiles.value = []
      return
    }
  }

  // 检查文件总大小
  const totalSize = files.reduce((sum, file) => sum + file.size, 0)
  const restriction = FILE_RESTRICTIONS[firstFileType]
  if (totalSize > restriction.maxSize * 3) { // 允许最多3个文件的总大小
    alert(`${firstFileType === 'image' ? '图片' : firstFileType === 'audio' ? '音频' : '视频'}文件总大小不能超过${(restriction.maxSize * 3) / 1024 / 1024}MB`)
    event.target.value = ''
    attachedFiles.value = []
    return
  }

  attachedFiles.value = files
}

// 修改文件输入提示
const getInputPlaceholder = () => {
  if (attachedFiles.value.length > 0) {
    const fileType = attachedFiles.value[0].type.split('/')[0]
    return `已选择${attachedFiles.value.length}个${FILE_RESTRICTIONS[fileType].description}，请输入您的问题...`
  }
  return '请输入您的问题，或上传文件...'
}

// 移除文件
const detachFile = (index) => {
  attachedFiles.value.splice(index, 1)
  if (fileSelector.value) {
    fileSelector.value.value = ''
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 发送消息
const submitMessage = async () => {
  if (isProcessing.value || (!userQuery.value.trim() && !attachedFiles.value.length)) return
  
  const messageContent = userQuery.value.trim()
  const files = [...attachedFiles.value]
  
  // 清空输入和文件
  userQuery.value = ''
  attachedFiles.value = []
  if (fileSelector.value) {
    fileSelector.value.value = ''
  }
  adjustTextareaHeight()
  
  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: messageContent,
    files: files,
    timestamp: new Date()
  }
  activeMessages.value.push(userMessage)
  await scrollToBottom()
  
  // 添加助手消息占位
  const assistantMessage = {
    role: 'assistant',
    content: '',
    timestamp: new Date()
  }
  activeMessages.value.push(assistantMessage)
  isProcessing.value = true
  
  try {
    const response = await chatAPI.sendMessage(messageContent, files, activeSessionId.value)
    assistantMessage.content = response.content
  } catch (error) {
    console.error('发送消息失败:', error)
    assistantMessage.content = '抱歉，发生了错误，请稍后重试。'
  } finally {
    isProcessing.value = false
    await scrollToBottom()
  }
}

// 加载特定会话
const switchSession = async (sessionId) => {
  activeSessionId.value = sessionId
  try {
    const messages = await chatAPI.getSessionMessages(sessionId)
    activeMessages.value = messages
  } catch (error) {
    console.error('加载会话消息失败:', error)
    activeMessages.value = []
  }
}

// 加载会话历史
const loadSessionHistory = async () => {
  try {
    const history = await chatAPI.getSessionHistory()
    sessionHistory.value = history || []
    if (history && history.length > 0) {
      await switchSession(history[0].id)
    } else {
      await initiateNewSession()
    }
  } catch (error) {
    console.error('加载会话历史失败:', error)
    sessionHistory.value = []
    await initiateNewSession()
  }
}

// 开始新会话
const initiateNewSession = async () => {
  const newSessionId = Date.now().toString()
  activeSessionId.value = newSessionId
  activeMessages.value = []
  
  // 添加新会话到历史列表
  const newSession = {
    id: newSessionId,
    title: `会话 ${newSessionId.slice(-6)}`
  }
  sessionHistory.value = [newSession, ...sessionHistory.value]
  
  // 发送初始问候语
  await submitMessage('你好，我是你的AI助手，有什么我可以帮你的吗？')
}

onMounted(() => {
  loadSessionHistory()
  adjustTextareaHeight()
})
</script>

<style scoped lang="scss">
.ai-companion {
  margin-top: 64px;
  position: fixed;
  top: 64px;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
  
  &.dark {
    background: #1a1a1a;
    color: #fff;
    
    .chat-container {
      background: #2d2d2d;
    }
    
    .sidebar {
      background: #333;
      border-right: 1px solid #444;
      
      .history-header {
        border-bottom: 1px solid #444;
        
        h2 {
          color: #fff;
        }
        
        .new-chat {
          background: #444;
          color: #fff;
          
          &:hover {
            background: #555;
          }
        }
      }
      
      .history-list {
        .history-item {
          color: #ccc;
          
          &:hover {
            background: #444;
          }
          
          &.active {
            background: #555;
            color: #fff;
          }
        }
      }
    }
    
    .chat-main {
      .messages {
        .message {
          &.user {
            background: #444;
          }
          
          &.assistant {
            background: #333;
          }
        }
      }
      
      .input-area {
        background: #333;
        border-top: 1px solid #444;
        
        .selected-files {
          background: #444;
          
          .file-item {
            border-bottom: 1px solid #555;
            
            .file-info {
              color: #ccc;
            }
            
            .remove-btn {
              color: #999;
              
              &:hover {
                color: #fff;
              }
            }
          }
        }
        
        .input-row {
          .file-upload {
            .upload-btn {
              color: #999;
              
              &:hover {
                color: #fff;
              }
            }
          }
          
          textarea {
            background: #444;
            color: #fff;
            
            &::placeholder {
              color: #999;
            }
          }
          
          .send-button {
            color: #999;
            
            &:hover {
              color: #fff;
            }
            
            &:disabled {
              color: #666;
            }
          }
        }
      }
    }
  }
  
  .chat-container {
    flex: 1;
    display: flex;
    background: #fff;
    margin: 1rem;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
  
  .sidebar {
    width: 280px;
    background: #f8f9fa;
    border-right: 1px solid #e9ecef;
    display: flex;
    flex-direction: column;
    
    .history-header {
      padding: 1rem;
      border-bottom: 1px solid #e9ecef;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h2 {
        margin: 0;
        font-size: 1.2rem;
        color: #333;
      }
      
      .new-chat {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        background: #e9ecef;
        border: none;
        border-radius: 4px;
        color: #495057;
        cursor: pointer;
        transition: all 0.2s;
        
        &:hover {
          background: #dee2e6;
        }
        
        .icon {
          width: 20px;
          height: 20px;
        }
      }
    }
    
    .history-list {
      flex: 1;
      overflow-y: auto;
      padding: 0.5rem;
      
      .history-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.2s;
        color: #495057;
        
        &:hover {
          background: #e9ecef;
        }
        
        &.active {
          background: #e9ecef;
          color: #212529;
        }
        
        .icon {
          width: 20px;
          height: 20px;
          color: #6c757d;
        }
        
        .title {
          flex: 1;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }
  
  .chat-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: #fff;
    
    .messages {
      flex: 1;
      overflow-y: auto;
      padding: 1rem;
      
      .message {
        max-width: 80%;
        margin-bottom: 1rem;
        padding: 1rem;
        border-radius: 8px;
        
        &.user {
          margin-left: auto;
          background: #e9ecef;
          color: #212529;
        }
        
        &.assistant {
          margin-right: auto;
          background: #f8f9fa;
          color: #212529;
        }
      }
    }
    
    .input-area {
      background: #f8f9fa;
      border-top: 1px solid #e9ecef;
      padding: 1rem;
      
      .selected-files {
        background: #fff;
        border: 1px solid #e9ecef;
        border-radius: 4px;
        margin-bottom: 1rem;
        
        .file-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 0.75rem;
          border-bottom: 1px solid #e9ecef;
          
          &:last-child {
            border-bottom: none;
          }
          
          .file-info {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: #495057;
            
            .icon {
              width: 20px;
              height: 20px;
              color: #6c757d;
            }
            
            .file-name {
              font-weight: 500;
            }
            
            .file-size {
              color: #6c757d;
            }
          }
          
          .remove-btn {
            background: none;
            border: none;
            padding: 0.25rem;
            color: #6c757d;
            cursor: pointer;
            transition: all 0.2s;
            
            &:hover {
              color: #dc3545;
            }
            
            .icon {
              width: 20px;
              height: 20px;
            }
          }
        }
      }
      
      .input-row {
        display: flex;
        gap: 0.5rem;
        
        .file-upload {
          position: relative;
          
          .hidden {
            display: none;
          }
          
          .upload-btn {
            background: none;
            border: none;
            padding: 0.5rem;
            color: #6c757d;
            cursor: pointer;
            transition: all 0.2s;
            
            &:hover {
              color: #495057;
            }
            
            &:disabled {
              color: #adb5bd;
              cursor: not-allowed;
            }
            
            .icon {
              width: 24px;
              height: 24px;
            }
          }
        }
        
        textarea {
          flex: 1;
          min-height: 50px;
          max-height: 200px;
          padding: 0.75rem;
          border: 1px solid #e9ecef;
          border-radius: 4px;
          resize: none;
          font-family: inherit;
          font-size: 1rem;
          line-height: 1.5;
          color: #212529;
          
          &:focus {
            outline: none;
            border-color: #86b7fe;
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
          }
          
          &::placeholder {
            color: #adb5bd;
          }
        }
        
        .send-button {
          background: none;
          border: none;
          padding: 0.5rem;
          color: #6c757d;
          cursor: pointer;
          transition: all 0.2s;
          
          &:hover {
            color: #495057;
          }
          
          &:disabled {
            color: #adb5bd;
            cursor: not-allowed;
          }
          
          .icon {
            width: 24px;
            height: 24px;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .ai-companion {
    .chat-container {
      margin: 0;
      border-radius: 0;
    }
    
    .sidebar {
      position: fixed;
      left: -280px;
      top: 64px;
      bottom: 0;
      z-index: 1000;
      transition: left 0.3s;
      
      &.show {
        left: 0;
      }
    }
  }
}
</style> 