const BASE_URL = 'http://localhost:6084'
const TIMEOUT = 30000 // 30秒超时

// 统一的错误处理
class APIError extends Error {
  constructor(message, status) {
    super(message)
    this.status = status
    this.name = 'APIError'
  }
}

// 统一的请求处理函数
async function fetchWithTimeout(url, options = {}) {
  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), TIMEOUT)
  
  try {
    const response = await fetch(url, {
      ...options,
      signal: controller.signal
    })
    
    if (!response.ok) {
      throw new APIError(`HTTP error! status: ${response.status}`, response.status)
    }
    
    return response
  } finally {
    clearTimeout(timeoutId)
  }
}

// 构建URL的辅助函数
function buildUrl(path, params = {}) {
  const url = new URL(`${BASE_URL}${path}`)
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      url.searchParams.append(key, value)
    }
  })
  return url
}

export const chatAPI = {
  // 发送聊天消息
  async simpleChat(data, chatId) {
    try {
      const url = buildUrl('/simple/chat', { chatId })
      const response = await fetchWithTimeout(url, {
        method: 'POST',
        body: data instanceof FormData ? data : new URLSearchParams({ prompt: data })
      })
      return response.body.getReader()
    } catch (error) {
      console.error('Simple Chat Error:', error)
      throw error
    }
  },

  // 获取聊天的历史会话id列表
  async historyChatIdList(type = 1) {
    try {
      const url = buildUrl('/history/chatId/list', { type })
      const response = await fetchWithTimeout(url)
      const chatIds = await response.json()
      
      return chatIds.map(id => ({
        id,
        title: type === 4 ? `PDF对话 ${id.slice(-6)}` : 
               type === 2 ? `咨询 ${id.slice(-6)}` :
               `对话 ${id.slice(-6)}`
      }))
    } catch (error) {
      console.error('History Chat ID List Error:', error)
      return []
    }
  },

  // 获取具体对话下的历史消息
  async historyChatHistoryList(chatId, type = 1) {
    try {
      const url = buildUrl('/history/chatHistory/list', { chatId, type })
      const response = await fetchWithTimeout(url)
      const messages = await response.json()
      
      return messages.map(msg => ({
        ...msg,
        timestamp: new Date()
      }))
    } catch (error) {
      console.error('History Chat History List Error:', error)
      return []
    }
  },

  // 发送助手消息
  async sendAssistantMessage(prompt, chatId) {
    try {
      const url = buildUrl('/program/chat', { prompt, chatId })
      const response = await fetchWithTimeout(url)
      return response.body.getReader()
    } catch (error) {
      console.error('Assistant Message Error:', error)
      throw error
    }
  },

  // 发送rag消息
  async sendRagMessage(prompt, chatId) {
    try {
      const url = buildUrl('/program/rag', { prompt, chatId })
      const response = await fetchWithTimeout(url)
      return response.body.getReader()
    } catch (error) {
      console.error('RAG Message Error:', error)
      throw error
    }
  },

  // 删除对话
  async deleteChat(chatId, type = 1) {
    try {
      const url = buildUrl('/history/delete', { chatId, type })
      await fetchWithTimeout(url)
      return true
    } catch (error) {
      console.error('Delete Chat Error:', error)
      throw error
    }
  }
} 