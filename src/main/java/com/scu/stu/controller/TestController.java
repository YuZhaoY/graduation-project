package com.scu.stu.controller;

import com.scu.stu.common.RedisLock;
import com.scu.stu.common.Result;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class TestController {

    @Autowired
    private RedisLock redisLock;

    private final String key = "userId";

    @GetMapping("api/testLogin")
    public Result testLogin(@RequestBody String userId) {
        if(redisLock.lock(key, userId, 10000)){
            return Result.success("缓存成功");
        } else {
            return Result.error("登录失败");
        }
    }

    @GetMapping("api/testFunction")
    public Result testFunction(@RequestBody String token) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            long expireTime = 30000; // expire time in milliseconds
            if(!redisLock.lock(key,userId,expireTime)){
                redisLock.unlock(key, userId);
                redisLock.lock(key, userId, expireTime);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/testlogout")
    public Result testlogout(@RequestBody String token) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            redisLock.unlock(key, userId);
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }
    }

}
