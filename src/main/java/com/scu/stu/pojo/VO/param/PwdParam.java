package com.scu.stu.pojo.VO.param;

import lombok.Data;

@Data
public class PwdParam {

    /**
     * token
     */
    private String token;

    /**
     * newPassword
     */
    private String password;
}
