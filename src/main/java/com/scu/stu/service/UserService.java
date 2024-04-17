package com.scu.stu.service;


import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.UserInfoDO;

import java.util.*;

public interface UserService {

    /**
     * 查询登录信息
     * @param username
     * @return
     */
    LoginInfoDO queryLoginInfo(String username);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    UserInfoDO queryUserInfo(String userId);

    /**
     * 批量查
     * @param userIdList
     */
    List<UserInfoDO> batchQueryUserInfo(List<String> userIdList);

    /**
     * 更新密码
     * @param loginInfoDO
     * @return
     */
    boolean updateLoginInfo(LoginInfoDO loginInfoDO);

    /**
     * 更新个人信息
     * @param userInfoDO
     * @return
     */
    boolean updateUserInfo(UserInfoDO userInfoDO);

    /**
     * 获取供应商列表
     * @return
     */
    List<UserInfoDO> queryUserListInfo(int role);
}
