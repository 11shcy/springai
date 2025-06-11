package org.javaup.ai.test;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.javaup.ai.ai.rag.MarkdownLoader;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RagAiTest {
    
    @Autowired
    private OpenAiEmbeddingModel embeddingModel;
    
    @Autowired
    private VectorStore vectorStore;
    
    @Autowired
    private MarkdownLoader markdownLoader;
    
    @PostConstruct
    public void testVectorStore(){
        //搜索条件
        SearchRequest request = SearchRequest.builder()
                .query("退票政策")
                .topK(1)
                .similarityThreshold(0.6)
                .similarityThresholdAll()
                .build();
        //查询
        List<Document> docs = vectorStore.similaritySearch(request);
        if (CollectionUtil.isEmpty(docs)) {
            log.info("====没有搜索到任何内容===");
            return;
        }
        log.info("====搜索到内容了===");
        for (Document doc : docs) {
            log.info(doc.getId());
            log.info(String.valueOf(doc.getScore()));
            log.info(doc.getText());
        }
    }
}
