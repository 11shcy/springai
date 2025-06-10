package org.javaup.ai.enums;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿星不是程序员 微信，添加时备注 大麦 来获取项目的完整资料 
 * @description: 通用状态枚举
 * @author: 阿星不是程序员
 **/

public enum ChatType {
    /**
     * 通用状态枚举
     * */
    CHAT(1,"普通会话"),
    DAMAI(2,"大麦智能客户"),
    PDF(3,"PDF智能助手"),
    ;

    private final Integer code;

    private final String msg;

    ChatType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }
    
    public String getMsg() {
        return this.msg == null ? "" : this.msg;
    }
    
    public static String getMsg(Integer code) {
        for (ChatType re : ChatType.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re.msg;
            }
        }
        return "";
    }

    public static ChatType getRc(Integer code) {
        for (ChatType re : ChatType.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re;
            }
        }
        return null;
    }
}
