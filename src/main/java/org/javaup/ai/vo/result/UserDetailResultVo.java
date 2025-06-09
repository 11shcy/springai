package org.javaup.ai.vo.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javaup.ai.vo.UserDetailVo;
import org.javaup.ai.vo.result.base.ApiResponse;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿星不是程序员 微信，添加时备注 大麦 来获取项目的完整资料 
 * @description: 用户 vo
 * @author: 阿星不是程序员
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDetailResultVo extends ApiResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private UserDetailVo data;

}
