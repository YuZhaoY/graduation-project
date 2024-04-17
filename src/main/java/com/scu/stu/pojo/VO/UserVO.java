package com.scu.stu.pojo.VO;

import lombok.Data;

import java.util.List;

@Data
public class UserVO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户角色
     */
    private List<String> roles;

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
