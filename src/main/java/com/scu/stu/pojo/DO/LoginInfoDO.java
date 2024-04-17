package com.scu.stu.pojo.DO;

import lombok.Data;

@Data
public class LoginInfoDO {

    /**
     * 用户名
     */
    String username;

    /**
     * 密码
     */
    String password;

    /**
     * 角色
     */
    int role;
}
