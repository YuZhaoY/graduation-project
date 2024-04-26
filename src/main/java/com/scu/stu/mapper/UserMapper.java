package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.UserInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface UserMapper {
    LoginInfoDO queryLoginInfo(String username);

    UserInfoDO queryUserInfo(String userId);

    List<UserInfoDO> batchQueryUserInfo(List<String> userIdList);

    boolean updateLoginInfo(LoginInfoDO loginInfoDO);

    boolean updateUserInfo(UserInfoDO loginInfoDO);

    List<UserInfoDO> queryUserListInfo(int role);

    boolean create(UserInfoDO userInfoDO);

    boolean createLogin(LoginInfoDO loginInfoDO);
}
