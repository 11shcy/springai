package org.javaup.ai.config;


import org.javaup.ai.ai.function.AiProgram;
import org.javaup.ai.ai.rag.MarkdownLoader;
import org.javaup.ai.ai.rag.MyKeywordEnricher;
import org.javaup.ai.constants.DaMaiConstant;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


public class DaMaiAiAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition");
    }

    @Bean
    public ChatClient chatClient(DeepSeekChatModel model) {
        return ChatClient
                .builder(model)
                .defaultOptions(ChatOptions.builder().model("qwen-omni-turbo").build())
                .defaultSystem("你是一个热心、可爱的智能助手，你的名字叫小团团，请以小团团的身份和语气回答问题。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }
    
    @Bean
    public MarkdownLoader markdownLoader(ResourcePatternResolver resourcePatternResolver){
        return new MarkdownLoader(resourcePatternResolver);
    }
    
    @Bean
    public MyKeywordEnricher myKeywordEnricher(DeepSeekChatModel model){
        return new MyKeywordEnricher(model);
    }

    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel,MarkdownLoader markdownLoader,
                                   MyKeywordEnricher myKeywordEnricher) {
        List<Document> documentList = markdownLoader.loadMarkdowns();
        List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documentList);
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        simpleVectorStore.add(enrichedDocuments);
        return simpleVectorStore;
    }

    @Bean
    public ChatClient damaiChatClient(DeepSeekChatModel model, ChatMemory chatMemory, AiProgram aiProgram) {
        return ChatClient
                .builder(model)
                .defaultSystem(DaMaiConstant.DA_MAI_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultTools(aiProgram)
                .build();
    }

    @Bean
    public ChatClient markdownChatClient(OpenAiChatModel model, ChatMemory chatMemory, VectorStore vectorStore) {
        return ChatClient
                .builder(model)
                .defaultSystem("请根据上下文回答问题，遇到上下文没有的问题，不要随意编造。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder().similarityThreshold(0.6).topK(3).build())
                                .build()
                )
                .build();
    }
}
