package org.javaup.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.javaup.ai.entity.ChatHistroy;
import org.javaup.ai.mapper.ChatHistroyMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @program: 大麦-ai智能服务项目。 添加 阿星不是程序员 微信，添加时备注 ai 来获取项目的完整资料 
 * @description: 聊天记录服务类
 * @author: 阿星不是程序员
 **/
@Service
public class ChatHistoryService {
    
    @Autowired
    private ChatHistroyMapper chatHistroyMapper;
    
    @Autowired 
    private ChatMemory chatMemory;
     
    
    /**
     * 保存会话记录
     * @param type 业务类型
     * @param chatId 会话ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(Integer type, String chatId){
        LambdaQueryWrapper<ChatHistroy> chatHistroyLambdaQueryWrapper =
                Wrappers.lambdaQuery(ChatHistroy.class).eq(ChatHistroy::getType, type).eq(ChatHistroy::getChatId, chatId);
        ChatHistroy chatHistroy = chatHistroyMapper.selectOne(chatHistroyLambdaQueryWrapper);
        if (Objects.isNull(chatHistroy)){
            chatHistroy = new ChatHistroy();
            chatHistroy.setType(type);
            chatHistroy.setChatId(chatId);
            chatHistroyMapper.insert(chatHistroy);
        }
    }
    
    /**
     * 获取会话ID列表
     * @param type 业务类型
     * @return 会话ID列表
     */
    public List<String> getChatIdList(Integer type){
        LambdaQueryWrapper<ChatHistroy> chatHistroyLambdaQueryWrapper =
                Wrappers.lambdaQuery(ChatHistroy.class).eq(ChatHistroy::getType, type);
        List<ChatHistroy> chatHistroyList = chatHistroyMapper.selectList(chatHistroyLambdaQueryWrapper);
        return chatHistroyList.stream()
                .map(ChatHistroy::getChatId)
                .toList();
    }
    
    /**
     * 删除会话记录
     * @param type 业务类型
     * @param chatId 会话ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer type, String chatId) {
        LambdaUpdateWrapper<ChatHistroy> chatHistroyLambdaUpdateWrapper =
                Wrappers.lambdaUpdate(ChatHistroy.class).eq(ChatHistroy::getType, type).eq(ChatHistroy::getChatId, chatId);
        chatHistroyMapper.delete(chatHistroyLambdaUpdateWrapper);
        chatMemory.clear(chatId);
    }
}
