package org.javaup.ai.cotroller;

import lombok.RequiredArgsConstructor;
import org.javaup.ai.common.ApiResponse;
import org.javaup.ai.service.impl.ChatHistoryServiceImpl;
import org.javaup.ai.vo.ChatHistoryMessageVO;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: 大麦-ai智能服务项目。 添加 阿星不是程序员 微信，添加时备注 ai 来获取项目的完整资料 
 * @description: 聊天记录控制器
 * @author: 阿星不是程序员
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/history")
public class ChatHistoryController {

    private final ChatHistoryServiceImpl chatHistoryServiceImpl;

    private final ChatMemory chatMemory;

    @RequestMapping("/chatId/list")
    public List<String> getChatIdList(@RequestParam("type") Integer type) {
        return chatHistoryServiceImpl.getChatIdList(type);
    }

    @RequestMapping("/chatHistory/list")
    public List<ChatHistoryMessageVO> getChatHistory(@RequestParam("chatId") String chatId,@RequestParam("type") Integer type) {
        List<Message> messages = chatMemory.get(chatId);
        return messages.stream().map(ChatHistoryMessageVO::new).toList();
    }
    
    @RequestMapping(value = "/delete")
    public ApiResponse<Void> delete(@RequestParam("type") Integer type, @RequestParam("chatId") String chatId){
        chatHistoryServiceImpl.delete(type, chatId);
        return ApiResponse.ok();
    }
}
