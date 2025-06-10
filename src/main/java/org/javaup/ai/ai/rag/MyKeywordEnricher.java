package org.javaup.ai.ai.rag;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;

import java.util.List;

/**
 * 基于 AI 的文档元信息增强器（为文档补充元信息）
 */
@AllArgsConstructor
public class MyKeywordEnricher {
    
    private final ChatModel chatModel;

    public List<Document> enrichDocuments(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatModel, 5);
        return  keywordMetadataEnricher.apply(documents);
    }
}
