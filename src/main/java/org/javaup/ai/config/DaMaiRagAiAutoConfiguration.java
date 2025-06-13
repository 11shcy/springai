package org.javaup.ai.config;

import org.javaup.ai.ai.rag.MarkdownLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.List;

import static org.javaup.ai.constants.DaMaiConstant.MARK_DOWN_SYSTEM_PROMPT;

@AutoConfigureAfter(DaMaiAiAutoConfiguration.class)
public class DaMaiRagAiAutoConfiguration {
    
    @Bean
    public MarkdownLoader markdownLoader(ResourcePatternResolver resourcePatternResolver){
        return new MarkdownLoader(resourcePatternResolver);
    }

    @Bean
    public ChatClient markdownChatClient(OpenAiChatModel model, ChatMemory chatMemory, VectorStore vectorStore,
                                         MarkdownLoader markdownLoader) {
        List<Document> documentList = markdownLoader.loadMarkdowns();
        vectorStore.add(documentList);
        
        return ChatClient
                .builder(model)
                .defaultSystem(MARK_DOWN_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .similarityThreshold(0.3)
                                        .topK(8)
                                        .build())
                                .build()
                )
                .build();
    }
}
