package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.Result;
import com.scu.stu.pojo.DO.queryParam.StoreQuery;
import com.scu.stu.pojo.DTO.StoreDTO;
import com.scu.stu.pojo.VO.StoreVO;
import com.scu.stu.pojo.VO.param.StoreParam;
import com.scu.stu.service.StoreService;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class StoreController {

    @Resource
    private StoreService storeService;

    @GetMapping(value = "api/getStoreListPage")
    public Result getStoreListPage(@RequestParam(value = "query") String data){
        StoreQuery query = new StoreQuery();
        if(data != null && !"".equals(data)){
            query= JSON.parseObject(data, StoreQuery.class);
        }
        List<StoreDTO> storeDTOS = storeService.query(query);
        if(CollectionUtils.isEmpty(storeDTOS)){
            return Result.success();
        } else {
            List<StoreVO> storeVOS = storeDTOS.stream().map(storeDTO -> {
                StoreVO storeVO = new StoreVO();
                BeanUtils.copyProperties(storeDTO, storeVO);
                return storeVO;
            }).collect(Collectors.toList());
            return Result.success(storeVOS);
        }
    }

    @GetMapping(value = "api/getStoreList")
    public Result getStoreList(@RequestParam(value = "query") String data){
        StoreQuery query = new StoreQuery();
        if(data != null && !"".equals(data)){
            query= JSON.parseObject(data, StoreQuery.class);
        }
        List<StoreDTO> storeDTOS = storeService.queryAll(query);
        if(CollectionUtils.isEmpty(storeDTOS)){
            return Result.success();
        } else {
            List<StoreVO> storeVOS = storeDTOS.stream().map(storeDTO -> {
                StoreVO storeVO = new StoreVO();
                BeanUtils.copyProperties(storeDTO, storeVO);
                return storeVO;
            }).collect(Collectors.toList());
            return Result.success(storeVOS);
        }
    }

    @GetMapping(value = "api/getStoreTotal")
    public Result getTotal(@RequestParam(value = "query") String data){
        StoreQuery query = new StoreQuery();
        if(data != null && !"".equals(data)){
            query= JSON.parseObject(data, StoreQuery.class);
        }
        int total = storeService.total(query);
        return Result.success(total);
    }

    @PutMapping(value = "api/createStore")
    public Result create(@RequestParam("data") String data){
        StoreParam storeParam = JSON.parseObject(data, StoreParam.class);
        String storeId = storeService.getStoreId();
        String newStoreId = String.format("%06d",Long.parseLong(storeId)+1);
        storeParam.setStoreId(newStoreId);
        StoreDTO storeDTO = new StoreDTO();
        BeanUtils.copyProperties(storeParam, storeDTO);
        if(storeService.create(storeDTO)){
            return Result.success();
        } else {
            return Result.error("创建仓失败");
        }
    }

    @PutMapping(value = "api/updateStore")
    public Result update(@RequestParam("data") String data){
        StoreParam storeParam = JSON.parseObject(data, StoreParam.class);
        StoreQuery query = new StoreQuery();
        query.setStoreId(storeParam.getStoreId());
        List<StoreDTO> storeDTOS = storeService.query(query);
        if(CollectionUtils.isEmpty(storeDTOS)){
            return Result.error("不存在ID为"+storeParam.getStoreId()+"的仓，请添加仓");
        }
        StoreDTO storeDTO = new StoreDTO();
        BeanUtils.copyProperties(storeParam, storeDTO);
        if(storeService.update(storeDTO)){
            return Result.success();
        } else {
            return Result.error("更新仓信息失败");
        }
    }
    @PutMapping(value = "api/deleteStore")
    public Result delete(@RequestParam("data") String storeId, @RequestParam("token") String token){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            if(storeId == null || "".equals(storeId)){
                return Result.error("ID为空");
            }
            if(storeService.delete(storeId)){
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }
}
