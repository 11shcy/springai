package org.javaup.ai.cotroller;

import org.javaup.ai.advisor.ChatTypeHistoryAdvisor;
import org.javaup.ai.ai.function.call.ProgramCall;
import org.javaup.ai.ai.function.dto.ProgramSearchFunctionDto;
import org.javaup.ai.ai.rag.QueryRewriter;
import org.javaup.ai.dto.ProgramDetailDto;
import org.javaup.ai.enums.ChatType;
import org.javaup.ai.service.ChatHistoryService;
import org.javaup.ai.vo.ProgramSearchVo;
import org.javaup.ai.vo.result.ProgramDetailResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @program: 大麦-ai智能服务项目。 添加 阿星不是程序员 微信，添加时备注 ai 来获取项目的完整资料 
 * @description: 节目控制器
 * @author: 阿星不是程序员
 **/
@RestController
@RequestMapping("/program")
public class ProgramController {

    private static final Logger log = LoggerFactory.getLogger(ProgramController.class);

    @Autowired
    private ProgramCall programCall;

    @Qualifier("assistantChatClient")
    @Autowired
    private ChatClient assistantChatClient;
    
    @Qualifier("markdownChatClient")
    @Autowired
    private ChatClient markdownChatClient;
    
    @Autowired
    private ChatHistoryService chatHistoryService;
    
    @Autowired
    private QueryRewriter queryRewriter;

    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam("prompt") String prompt,
                                @RequestParam("chatId") String chatId) {
        // 1.保存会话id，转为自定义advisors形式执行
        //chatHistoryService.save(ChatType.ASSISTANT.getCode(), chatId);
        // 2.请求模型
        return assistantChatClient.prompt()
                .user(prompt)
                .advisors(ChatTypeHistoryAdvisor.builder(chatHistoryService).type(ChatType.ASSISTANT.getCode()).build())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
    
    @RequestMapping(value = "/rag", produces = "text/html;charset=utf-8")
    public Flux<String> rag(@RequestParam("prompt") String prompt,
                             @RequestParam("chatId") String chatId) {
        /// 1.保存会话id
        //chatHistoryService.save(ChatType.MARKDOWN.getCode(), chatId);
        // 2.请求模型
        return markdownChatClient.prompt()
                .user(prompt)
                .advisors(ChatTypeHistoryAdvisor.builder(chatHistoryService).type(ChatType.MARKDOWN.getCode()).build())
                .advisors(a -> {
                    log.info("设置会话ID: {}", chatId);
                    a.param(ChatMemory.CONVERSATION_ID, chatId);
                })
                .stream()
                .content();
    }
    
    @PostMapping(value = "/search")
    public List<ProgramSearchVo> search(@RequestBody ProgramSearchFunctionDto programSearchFunctionDto) {
        return programCall.search(programSearchFunctionDto);
    }

    @PostMapping(value = "/detail")
    public ProgramDetailResultVo search(@RequestBody ProgramDetailDto programDetailDto) {
        return programCall.detail(programDetailDto);
    }
}
