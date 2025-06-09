package org.javaup.ai.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import org.javaup.ai.enums.BaseCode;
import org.javaup.ai.vo.TicketUserVo;
import org.javaup.ai.vo.UserDetailVo;
import org.javaup.ai.vo.result.TicketUserResultVo;
import org.javaup.ai.vo.result.UserDetailResultVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.javaup.ai.constants.DaMaiConstant.TICKET_USER_LIST_URL;
import static org.javaup.ai.constants.DaMaiConstant.USER_DETAIL_URL;

@Service
public class UserService {
    
    public UserDetailVo userDetail(String mobile){
        Map<String,String> params = new HashMap<>(2);
        params.put("mobile", mobile);
        UserDetailResultVo userDetailResultVo = new UserDetailResultVo();
        String result = HttpRequest.post(USER_DETAIL_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(params))
                .timeout(20000)
                .execute().body();
        userDetailResultVo = JSON.parseObject(result, UserDetailResultVo.class);
        if (!Objects.equals(userDetailResultVo.getCode(), BaseCode.SUCCESS.getCode())) {
            return null;
        }
        return userDetailResultVo.getData();
    }
    
    public List<TicketUserVo> ticketUserList(Long userId){
        List<TicketUserVo> ticketUserVoList = new ArrayList<>();
        Map<String,Object> params = new HashMap<>(2);
        params.put("userId", userId);
        TicketUserResultVo ticketUserResultVo = new TicketUserResultVo();
        String result = HttpRequest.post(TICKET_USER_LIST_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(params))
                .timeout(20000)
                .execute().body();
        ticketUserResultVo = JSON.parseObject(result, TicketUserResultVo.class);
        if (!Objects.equals(ticketUserResultVo.getCode(), BaseCode.SUCCESS.getCode()) || Objects.isNull(ticketUserResultVo.getData())) {
            return ticketUserVoList;
        }
        return ticketUserResultVo.getData();
    }
}
