package org.javaup.ai.service;


import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.javaup.ai.dto.ProgramDetailDto;
import org.javaup.ai.ai.function.dto.ProgramRecommendFunctionDto;
import org.javaup.ai.ai.function.dto.ProgramSearchFunctionDto;
import org.javaup.ai.enums.BaseCode;
import org.javaup.ai.es.mapper.ProgramMapper;
import org.javaup.ai.utils.StringUtil;
import org.javaup.ai.vo.result.ProgramDetailResultVo;
import org.javaup.ai.vo.ProgramSearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static org.javaup.ai.constants.DaMaiConstant.PROGRAM_DETAIL_URL;

@Service
public class ProgramService {

    @Autowired
    private ProgramMapper programMapper;
    
    public List<ProgramSearchVo> recommendList(ProgramRecommendFunctionDto programRecommendFunctionDto){
        LambdaEsQueryWrapper<ProgramSearchVo> wrapper = EsWrappers.lambdaQuery(ProgramSearchVo.class)
                .eq(StringUtil.isNotEmpty(programRecommendFunctionDto.getAreaName()), ProgramSearchVo::getAreaName, programRecommendFunctionDto.getAreaName())
                .eq(StringUtil.isNotEmpty(programRecommendFunctionDto.getProgramCategory()), ProgramSearchVo::getParentProgramCategoryName, programRecommendFunctionDto.getProgramCategory());
        return programMapper.selectList(wrapper);
    }

    public List<ProgramSearchVo> search(ProgramSearchFunctionDto programSearchFunctionDto){
        LambdaEsQueryWrapper<ProgramSearchVo> wrapper = EsWrappers.lambdaQuery(ProgramSearchVo.class)
                .eq(StringUtil.isNotEmpty(programSearchFunctionDto.getCityName()), ProgramSearchVo::getAreaName, programSearchFunctionDto.getCityName())
                .eq(StringUtil.isNotEmpty(programSearchFunctionDto.getActor()), ProgramSearchVo::getActor, programSearchFunctionDto.getActor())
                .ge(Objects.nonNull(programSearchFunctionDto.getShowTime()), ProgramSearchVo::getShowTime, programSearchFunctionDto.getShowTime());
        return programMapper.selectList(wrapper);
    }

    public ProgramDetailResultVo detail(ProgramDetailDto programDetailDto) {
        ProgramDetailResultVo programDetailResultVo = new ProgramDetailResultVo();
        String result = HttpRequest.post(PROGRAM_DETAIL_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(programDetailDto))
                .timeout(20000)
                .execute().body();
        programDetailResultVo = JSON.parseObject(result, ProgramDetailResultVo.class);
        if (!Objects.equals(programDetailResultVo.getCode(), BaseCode.SUCCESS.getCode())) {
            return programDetailResultVo;
        }
        return programDetailResultVo;
    }
}
