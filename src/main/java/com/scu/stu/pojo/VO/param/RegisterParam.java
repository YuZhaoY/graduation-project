package com.scu.stu.pojo.VO.param;

import lombok.Data;

@Data
public class RegisterParam {

    /**
     * 用户名
     */
    String username;

    /**
     * 密码
     */
    String password;
    /**
     * 名称
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 角色
     */
    private int role;

    /**
     * 年龄
     */
    private int age;

    /**
     * 身份证号
     */
    private String identityCard;
}
