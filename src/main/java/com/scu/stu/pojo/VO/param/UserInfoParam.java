package com.scu.stu.pojo.VO.param;

import lombok.Data;

@Data
public class UserInfoParam {

    /**
     * 名称
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 个人介绍
     */
    private String introduction;

    /**
     * 年龄
     */
    private int age;

    /**
     * 头像
     */
    private String avatar;
}
