package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.RedisLock;
import com.scu.stu.common.Result;
import com.scu.stu.pojo.DO.queryParam.ItemQuery;
import com.scu.stu.pojo.DTO.ItemDTO;
import com.scu.stu.pojo.VO.param.ItemParam;
import com.scu.stu.pojo.VO.ItemVO;
import com.scu.stu.service.ItemService;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class ItemController {

    @Autowired
    private RedisLock redisLock;

    @Resource
    private ItemService itemService;

    private final String tokenCheck = "token";

    private final long expireTime = 30*60*1000; //30分钟

    @GetMapping("api/getItemList")
    public Result getItemList(@RequestParam(value = "query") String data){
        ItemQuery query = new ItemQuery();
        if(data != null && !"".equals(data)){
            query=JSON.parseObject(data, ItemQuery.class);
        }
        List<ItemDTO> itemDTOList = itemService.query(query);
        if(CollectionUtils.isEmpty(itemDTOList)){
            return Result.success();
        }
        List<ItemVO> result= itemDTOList.stream().map(itemDTO -> {
            ItemVO itemVO = new ItemVO();
            BeanUtils.copyProperties(itemDTO,itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @GetMapping("api/getTotal")
    public Result getTotal(@RequestParam(value = "query") String data){
        ItemQuery query = new ItemQuery();
        if(data != null && !"".equals(data)){
            query=JSON.parseObject(data, ItemQuery.class);
        }
        int total = itemService.total(query);
        return Result.success(total);
    }

    @PutMapping("api/createItem")
    public Result create(@RequestParam("data") String data){
        ItemParam itemParam = JSON.parseObject(data, ItemParam.class);
        String itemId = itemService.getItemId();
        String newItemId = String.format("%06d",Long.parseLong(itemId)+1);
        itemParam.setItemId(newItemId);
        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(itemParam, itemDTO);
        if(itemService.create(itemDTO)){
            return Result.success();
        } else {
            return Result.error("上传货品失败");
        }
    }

    @PutMapping("api/updateItem")
    public Result update(@RequestParam("data") String data){
        ItemParam itemParam = JSON.parseObject(data, ItemParam.class);
        ItemQuery query = new ItemQuery();
        query.setItemId(itemParam.getItemId());
        List<ItemDTO> itemDTOS = itemService.query(query);
        if(CollectionUtils.isEmpty(itemDTOS)){
            return Result.error("不存在ID为"+itemParam.getItemId()+"的货品，请添加货品");
        }
        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(itemParam, itemDTO);
        if(itemService.update(itemDTO)){
            return Result.success();
        } else {
            return Result.error("更新货品失败");
        }
    }
    @PutMapping("api/delete")
    public Result delete(@RequestParam("data") String itemId, @RequestParam("token") String token){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            if(itemId == null || "".equals(itemId)){
                return Result.error("ID为空");
            }
            if(itemService.delete(itemId)){
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }

    public boolean RefreshToken(String userId) {
        if(!redisLock.lock(userId, tokenCheck, expireTime)){
            redisLock.unlock(userId,tokenCheck);
            redisLock.lock(userId,tokenCheck,expireTime);
            return true;
        } else {
            redisLock.unlock(userId, tokenCheck);
            return false;
        }
    }
}
