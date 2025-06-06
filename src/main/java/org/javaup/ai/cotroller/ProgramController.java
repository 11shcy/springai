package org.javaup.ai.cotroller;

import org.javaup.ai.dto.ProgramDetailDto;
import org.javaup.ai.dto.ProgramSearchDto;
import org.javaup.ai.repository.ChatHistoryRepository;
import org.javaup.ai.service.ProgramService;
import org.javaup.ai.vo.ProgramDetailResultVo;
import org.javaup.ai.vo.ProgramSearchVo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.javaup.ai.constants.DaMaiConstant.DA_MAI_TYPE;

@RestController
@RequestMapping("/program")
public class ProgramController {

    @Autowired
    private ProgramService programService;

    @Qualifier("damaiChatClient")
    @Autowired
    private ChatClient serviceChatClient;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/ai", produces = "text/html;charset=utf-8")
    public Flux<String> service(@RequestParam("prompt") String prompt,
                                @RequestParam("chatId") String chatId) {
        // 1.保存会话id
        chatHistoryRepository.save(DA_MAI_TYPE, chatId);
        // 2.请求模型
        return serviceChatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    @PostMapping(value = "/search")
    public List<ProgramSearchVo> search(@RequestBody ProgramSearchDto programSearchDto) {
        return programService.search(programSearchDto);
    }

    @PostMapping(value = "/detail")
    public ProgramDetailResultVo search(@RequestBody ProgramDetailDto programDetailDto) {
        return programService.detail(programDetailDto);
    }
}
