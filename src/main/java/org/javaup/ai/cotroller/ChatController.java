package org.javaup.ai.cotroller;


import org.javaup.ai.enums.ChatType;
import org.javaup.ai.service.ChatHistoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;



@RestController
@RequestMapping("/ai")
public class ChatController {

    @Qualifier("chatClient")
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatHistoryService chatHistoryService;


    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam("prompt") String prompt, @RequestParam("chatId") String chatId) {
        // 1.保存会话id
        chatHistoryService.save(ChatType.CHAT.getCode(), chatId);
        // 2.请求模型
        return chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(prompt)
                .stream()
                .content();
    }
}
