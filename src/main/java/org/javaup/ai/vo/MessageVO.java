package org.javaup.ai.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import static org.springframework.ai.chat.messages.MessageType.ASSISTANT;
import static org.springframework.ai.chat.messages.MessageType.USER;

@NoArgsConstructor
@Data
public class MessageVO {
    private String role;
    private String content;

    public MessageVO(Message message) {
        MessageType messageType = message.getMessageType();
        if (messageType == USER) {
            this.role = "user";
        }else if (messageType == ASSISTANT) {
            this.role = "assistant";
        }else {
            this.role = "";
        }
        this.content = message.getText();
    }
}
