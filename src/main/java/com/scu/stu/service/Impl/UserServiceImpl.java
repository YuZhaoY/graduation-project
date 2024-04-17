package com.scu.stu.service.Impl;


import com.scu.stu.mapper.UserMapper;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.UserInfoDO;
import com.scu.stu.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public LoginInfoDO queryLoginInfo(String username) {
        return userMapper.queryLoginInfo(username);
    }

    @Override
    public UserInfoDO queryUserInfo(String userId) {
        return userMapper.queryUserInfo(userId);
    }

    @Override
    public List<UserInfoDO> batchQueryUserInfo(List<String> userIdList) {
        return userMapper.batchQueryUserInfo(userIdList);
    }

    @Override
    public boolean updateLoginInfo(LoginInfoDO loginInfoDO) {
        return userMapper.updateLoginInfo(loginInfoDO);
    }

    @Override
    public boolean updateUserInfo(UserInfoDO userInfoDO) {
        return userMapper.updateUserInfo(userInfoDO);
    }

    @Override
    public List<UserInfoDO> queryUserListInfo(int role) {
        return userMapper.queryUserListInfo(role);
    }
}
