package com.scu.stu.pojo.VO.param;

import lombok.Data;

@Data
public class LoginParam {
    /**
     * 用户名
     */
    String username;

    /**
     * 密码
     */
    String password;
}
