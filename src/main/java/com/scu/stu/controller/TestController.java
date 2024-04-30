package com.scu.stu.controller;

import com.scu.stu.common.RedisLock;
import com.scu.stu.common.Result;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import com.scu.stu.pojo.DTO.RelationDTO;
import com.scu.stu.pojo.DTO.SaleDTO;
import com.scu.stu.service.RelationService;
import com.scu.stu.service.SaleService;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class TestController {

    @Autowired
    private RedisLock redisLock;

    @Resource
    private RelationService relationService;

    @Resource
    private SaleService saleService;

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

    @PostMapping("api/test")
    public Result test() {
        SaleQuery query = new SaleQuery();
        query.setFarmerId("12313");
        List<SaleDTO> saleDTOS = saleService.getList(query);
        List<String> saleIdList = saleDTOS.stream().map(SaleDTO::getSaleId).collect(Collectors.toList());
        List<RelationDTO> relationDTOS = relationService.queryBySaleIdList(saleIdList);
        List<Integer> idList = relationDTOS.stream().map(RelationDTO::getId).collect(Collectors.toList());
        RelationQuery relationQuery = new RelationQuery();
        List<RelationDTO> relationDTOList = relationService.queryByIdListPage(idList, relationQuery);
        return Result.success(relationDTOList);
    }

}
