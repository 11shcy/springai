package org.javaup.ai.ai.function;

import cn.hutool.core.collection.CollectionUtil;
import org.javaup.ai.dto.ProgramDetailDto;
import org.javaup.ai.dto.ProgramRecommendDto;
import org.javaup.ai.dto.ProgramSearchDto;
import org.javaup.ai.dto.TicketCategoryListByProgramDto;
import org.javaup.ai.service.ProgramService;
import org.javaup.ai.service.TicketCategoryService;
import org.javaup.ai.vo.ProgramDetailResultVo;
import org.javaup.ai.vo.ProgramDetailVo;
import org.javaup.ai.vo.ProgramSearchVo;
import org.javaup.ai.vo.TicketCategoryDetailVo;
import org.javaup.ai.vo.TicketCategoryVo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AiProgram {

    @Autowired
    private ProgramService programService;

    @Autowired
    private TicketCategoryService ticketCategoryService;
    
    @Tool(description = "根据地区或者类型查询推荐的节目")
    public List<ProgramSearchVo> selectProgramRecommendList(@ToolParam(description = "查询的条件", required = false) ProgramRecommendDto programRecommendDto){
        return programService.recommendList(programRecommendDto);
    }

    @Tool(description = "根据条件查询节目")
    public List<ProgramSearchVo> selectProgramList(@ToolParam(description = "查询的条件", required = false) ProgramSearchDto programSearchDto){
        return programService.search(programSearchDto);
    }

    @Tool(description = "根据条件查询节目的票档信息")
    public List<ProgramDetailVo> selectTicketCategory(@ToolParam(description = "查询的条件", required = false) ProgramSearchDto programSearchDto){
        List<ProgramDetailVo> programDetailVoList = new ArrayList<>();
        List<ProgramSearchVo> programSearchVoList = programService.search(programSearchDto);
        if (CollectionUtil.isEmpty(programSearchVoList)) {
            return programDetailVoList;
        }
        for (ProgramSearchVo programSearchVo : programSearchVoList) {
            ProgramDetailDto programDetailDto = new ProgramDetailDto();
            programDetailDto.setId(programSearchVo.getId());
            ProgramDetailResultVo programDetailResultVo = programService.detail(programDetailDto);
            if (Objects.nonNull(programDetailResultVo.getData())) {
                ProgramDetailVo programDetailVo = programDetailResultVo.getData();
                TicketCategoryListByProgramDto ticketCategoryListByProgramDto = new TicketCategoryListByProgramDto();
                ticketCategoryListByProgramDto.setProgramId(programDetailVo.getId());
                List<TicketCategoryDetailVo> ticketCategoryDetailVoList = ticketCategoryService.selectListByProgram(ticketCategoryListByProgramDto);
                Map<Long, TicketCategoryDetailVo> ticketCategoryDetailMap = ticketCategoryDetailVoList.stream()
                        .collect(Collectors.toMap(TicketCategoryDetailVo::getId,
                                ticketCategoryDetailVo -> ticketCategoryDetailVo,
                                (v1, v2) -> v2));
                for (TicketCategoryVo ticketCategoryVo : programDetailVo.getTicketCategoryVoList()) {
                    TicketCategoryDetailVo ticketCategoryDetailVo = ticketCategoryDetailMap.get(ticketCategoryVo.getId());
                    if (Objects.nonNull(ticketCategoryDetailVo)) {
                        ticketCategoryVo.setRemainNumber(ticketCategoryDetailVo.getRemainNumber());
                        ticketCategoryVo.setTotalNumber(ticketCategoryDetailVo.getTotalNumber());
                    }
                }
                programDetailVoList.add(programDetailResultVo.getData());
            }
        }
        return programDetailVoList;
    }
}
