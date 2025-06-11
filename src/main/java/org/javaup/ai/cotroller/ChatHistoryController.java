package org.javaup.ai.cotroller;

import lombok.RequiredArgsConstructor;
import org.javaup.ai.common.ApiResponse;
import org.javaup.ai.service.ChatHistoryService;
import org.javaup.ai.vo.ChatHistoryMessageVO;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/history")
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    private final ChatMemory chatMemory;

    @RequestMapping("/chatId/list")
    public List<String> getChatIdList(@RequestParam("type") Integer type) {
        return chatHistoryService.getChatIdList(type);
    }

    @RequestMapping("/chatHistory/list")
    public List<ChatHistoryMessageVO> getChatHistory(@RequestParam("chatId") String chatId,@RequestParam("type") Integer type) {
        List<Message> messages = chatMemory.get(chatId);
        return messages.stream().map(ChatHistoryMessageVO::new).toList();
    }
    
    @RequestMapping(value = "/delete")
    public ApiResponse<Void> delete(@RequestParam("type") Integer type, @RequestParam("chatId") String chatId){
        chatHistoryService.delete(type, chatId);
        return ApiResponse.ok();
    }
}
