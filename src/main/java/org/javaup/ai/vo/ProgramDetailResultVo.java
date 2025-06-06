package org.javaup.ai.vo;

import lombok.Data;
import org.javaup.ai.vo.base.ApiResponse;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ProgramDetailResultVo extends ApiResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ProgramDetailVo data;
}
