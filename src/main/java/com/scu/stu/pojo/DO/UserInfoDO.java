package com.scu.stu.pojo.DO;

import lombok.Data;

@Data
public class UserInfoDO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户角色
     */
    private int role;

    /**
     * 用户介绍
     */
    private String introduction;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄
     */
    private int age;
}
