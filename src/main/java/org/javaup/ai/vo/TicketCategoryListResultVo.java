package org.javaup.ai.vo;

import lombok.Data;
import org.javaup.ai.vo.base.ApiResponse;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class TicketCategoryListResultVo extends ApiResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<TicketCategoryDetailVo> data;
}
