package org.javaup.ai.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import org.javaup.ai.dto.ProgramOrderCreateDto;
import org.javaup.ai.enums.BaseCode;
import org.javaup.ai.vo.result.CreateOrderResult;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.javaup.ai.constants.DaMaiConstant.CREATE_ORDER_URL;

@Service
public class OrderService {
    
    public String createOrder(ProgramOrderCreateDto programOrderCreateDto){
        CreateOrderResult createOrderResult = new CreateOrderResult();
        String result = HttpRequest.post(CREATE_ORDER_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(programOrderCreateDto))
                .timeout(20000)
                .execute().body();
        createOrderResult = JSON.parseObject(result, CreateOrderResult.class);
        if (!Objects.equals(createOrderResult.getCode(), BaseCode.SUCCESS.getCode())) {
            throw new RuntimeException("调用大麦系统创建订单失败");
        }
        return createOrderResult.getData();
    }
}
