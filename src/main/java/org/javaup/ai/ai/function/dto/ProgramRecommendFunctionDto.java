package org.javaup.ai.ai.function.dto;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿星不是程序员 微信，添加时备注 大麦 来获取项目的完整资料 
 * @description: 节目搜索 dto
 * @author: 阿星不是程序员
 **/
@Data
public class ProgramRecommendFunctionDto {

    @ToolParam(required = false, description = "节目演出地点")
    private String areaName;

    @ToolParam(required = false, description = "节目类型")
    private String programCategory;
}
