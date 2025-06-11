const BASE_URL = 'http://localhost:6084'

export const chatAPI = {
  // 发送聊天消息
  async simpleChat(data, chatId) {
    try {
      const url = new URL(`${BASE_URL}/simple/chat`)
      if (chatId) {
        url.searchParams.append('chatId', chatId)
      }
      
      const response = await fetch(url, {
        method: 'POST',
        body: data instanceof FormData ? data : 
          new URLSearchParams({ prompt: data })
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.body.getReader()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },

  // 获取聊天的历史会话id列表
  async historyChatIdList(type = 1) {  // 添加类型参数
    try {
      const response = await fetch(`${BASE_URL}/history/chatId/list?type=${type}`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      const chatIds = await response.json()
      // 转换为前端需要的格式
      return chatIds.map(id => ({
        id,
        title: type === 4 ? `PDF对话 ${id.slice(-6)}` : 
               type === 2 ? `咨询 ${id.slice(-6)}` :
               `对话 ${id.slice(-6)}`
      }))
    } catch (error) {
      console.error('API Error:', error)
      return []
    }
  },

  // 获取具体对话下的历史消息
  async historyChatHistoryList(chatId, type = 1) {  // 添加类型参数
    try {
      const response = await fetch(`${BASE_URL}/history/chatHistory/list?chatId=${chatId}&type=${type}`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      const messages = await response.json()
      // 添加时间戳
      return messages.map(msg => ({
        ...msg,
        timestamp: new Date() // 由于后端没有提供时间戳，这里临时使用当前时间
      }))
    } catch (error) {
      console.error('API Error:', error)
      return []
    }
  },
  // 发送助手消息
  async sendAssistantMessage(prompt, chatId) {
    try {
      const response = await fetch(`${BASE_URL}/program/chat?prompt=${encodeURIComponent(prompt)}&chatId=${chatId}`, {
        method: 'GET',
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.body.getReader()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },
  // 发送rag消息
  async sendRagMessage(prompt, chatId) {
    try {
      const response = await fetch(`${BASE_URL}/program/rag?prompt=${encodeURIComponent(prompt)}&chatId=${chatId}`, {
        method: 'GET',
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return response.body.getReader()
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  },
  // 删除对话
  async deleteChat(chatId, type = 1) {
    try {
      const response = await fetch(`${BASE_URL}/history/delete?chatId=${chatId}&type=${type}`, {
        method: 'GET',
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      return true
    } catch (error) {
      console.error('API Error:', error)
      throw error
    }
  }
} 