package org.javaup.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javaup.ai.entity.base.BaseTableData;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("d_chat_history")
public class ChatHistroy extends BaseTableData {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Integer type;
    
    private String chatId;
}
