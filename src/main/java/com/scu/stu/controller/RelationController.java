package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.RedisLock;
import com.scu.stu.common.Result;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import com.scu.stu.pojo.DTO.RelationDTO;
import com.scu.stu.pojo.DTO.SaleDTO;
import com.scu.stu.pojo.VO.RelationVO;
import com.scu.stu.service.RelationService;
import com.scu.stu.service.SaleService;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class RelationController {

    @Autowired
    private RedisLock redisLock;

    @Resource
    private RelationService relationService;

    @Resource
    private SaleService saleService;

    private final String tokenCheck = "token";

    private final long expireTime = 30*60*1000; //30分钟

    @PostMapping("api/queryRelationByBookingList")
    public Result queryRelationByBookingList(@RequestBody List<String> bookingList) {
        if (bookingList != null && !CollectionUtils.isEmpty(bookingList)) {
            List<RelationDTO> relationDTOList = relationService.queryByBookingList(bookingList);
            if (relationDTOList != null && !CollectionUtils.isEmpty(relationDTOList)) {
                List<RelationVO> relationVOList = relationDTOList.stream().map(relationDTO -> {
                    RelationVO relationVO = new RelationVO();
                    BeanUtils.copyProperties(relationDTO, relationVO);
                    return relationVO;
                }).collect(Collectors.toList());
                return Result.success(relationVOList);
            }
        }
        return Result.success();
    }

    @PostMapping("api/queryRelation")
    public Result getList(@RequestBody RelationQuery query) {
        List<RelationDTO> relationDTOS = relationService.query(query);
        if(relationDTOS != null && !CollectionUtils.isEmpty(relationDTOS)){
            List<RelationVO> relationVOList = relationDTOS.stream().map(relationDTO -> {
                RelationVO relationVO = new RelationVO();
                BeanUtils.copyProperties(relationDTO, relationVO);
                return relationVO;
            }).collect(Collectors.toList());
            return Result.success(relationVOList);
        }
        return Result.success();
    }

    @PostMapping("api/queryEditorRelation")
    public Result getEditorList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            RelationQuery query = JSON.parseObject(data, RelationQuery.class);
            SaleQuery saleQuery = new SaleQuery();
            saleQuery.setPage(query.getPage());
            saleQuery.setLimit(query.getLimit());
            saleQuery.setFarmerId(userId);
            List<SaleDTO> saleDTOS = saleService.getList(saleQuery);
            if(saleDTOS != null && !CollectionUtils.isEmpty(saleDTOS)){
                List<String> saleIdList = saleDTOS.stream().map(SaleDTO::getSaleId).collect(Collectors.toList());
                List<RelationDTO> relationBySale = relationService.queryBySaleIdList(saleIdList);
                List<RelationDTO> relationByQuery = relationService.query(query);
                List<RelationDTO> result = new ArrayList<>();
                for(RelationDTO relation : relationByQuery) {
                    for(RelationDTO relation2 : relationBySale){
                        if(relation.getId() == relation2.getId()){
                            result.add(relation);
                            break;
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(result)){
                    List<RelationVO> relationVOList = result.stream().map(relationDTO -> {
                        RelationVO relationVO = new RelationVO();
                        BeanUtils.copyProperties(relationDTO, relationVO);
                        return relationVO;
                    }).collect(Collectors.toList());
                    return Result.success(relationVOList);
                }
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping("api/queryEditorTotal")
    public Result editorTotal(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            RelationQuery query = JSON.parseObject(data, RelationQuery.class);
            SaleQuery saleQuery = new SaleQuery();
            saleQuery.setPage(query.getPage());
            saleQuery.setLimit(query.getLimit());
            saleQuery.setFarmerId(userId);
            List<SaleDTO> saleDTOS = saleService.getList(saleQuery);
            if(saleDTOS != null && !CollectionUtils.isEmpty(saleDTOS)){
                List<String> saleIdList = saleDTOS.stream().map(SaleDTO::getSaleId).collect(Collectors.toList());
                List<RelationDTO> relationBySale = relationService.queryBySaleIdList(saleIdList);
                List<RelationDTO> relationByQuery = relationService.query(query);
                List<RelationDTO> result = new ArrayList<>();
                for(RelationDTO relation : relationByQuery) {
                     for(RelationDTO relation2 : relationBySale){
                         if(relation.getId() == relation2.getId()){
                             result.add(relation);
                             break;
                         }
                     }
                }
                return Result.success(result.size());
            }
            return Result.success(0);
        } else {
            return Result.error("未查到身份信息");
        }
    }
    @PostMapping("api/queryRelationTotal")
    public Result total(@RequestBody RelationQuery query) {
        return Result.success(relationService.total(query));
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
