package org.javaup.ai.cotroller;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;
    private Object chatId;

    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam("prompt") String prompt, @RequestParam("chatId") String chatId) {
        // 1.保存会话id
        //chatHistoryRepository.save("chat", chatId);
        // 2.请求模型
        return chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(prompt)
                .stream()
                .content();
    }
}
