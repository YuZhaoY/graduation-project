package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.Result;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.common.SaleStatus;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import com.scu.stu.pojo.DTO.RelationDTO;
import com.scu.stu.pojo.DTO.SaleDTO;
import com.scu.stu.pojo.DTO.SaleSubDTO;
import com.scu.stu.pojo.VO.SaleSubVO;
import com.scu.stu.pojo.VO.SaleVO;
import com.scu.stu.pojo.VO.param.SaleParam;
import com.scu.stu.service.SaleService;
import com.scu.stu.service.UserService;
import com.scu.stu.utils.DateUtils;
import com.scu.stu.utils.JwtUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import wiki.xsx.core.snowflake.config.Snowflake;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class SaleController {

    @Resource
    private SaleService saleService;

    @Resource
    private UserService userService;

    @Autowired
    private Snowflake snowflake;

    @Resource(name = "rocketMQService")
    private RocketMQTemplate rocketMQService;

    @Value("${rocketmq.topic.relation}")
    private String topic;

    @GetMapping(value = "api/getSaleOrderList")
    public Result getSaleOrderList(@RequestParam("token") String token, @RequestParam("data") String data){
        String tokenValue = JwtUtils.verity(token);
        if(tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            SaleQuery query = JSON.parseObject(data, SaleQuery.class);
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if(loginInfoDO.getRole() == RoleEnum.ADMIN.getRole()){
                query.setPurchaseId(userId);
            }else {
                query.setFarmerId(userId);
            }
            List<SaleDTO> saleDTOS = saleService.getList(query);
            if(saleDTOS != null && !CollectionUtils.isEmpty(saleDTOS)){
                List<SaleVO> saleVOS = saleDTOS.stream().map(saleDTO -> {
                    SaleVO saleVO  = new SaleVO();
                    BeanUtils.copyProperties(saleDTO, saleVO);
                    saleVO.setGMTCreate(DateUtils.format(saleDTO.getGMTCreate()));
                    saleVO.setStatus(SaleStatus.getDescByCode(saleDTO.getStatus()));
                    return saleVO;
                }).collect(Collectors.toList());
                return Result.success(saleVOS);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping(value = "api/getSaleTotal")
    public Result total(@RequestParam("token") String token, @RequestParam("data") String data){
        String tokenValue = JwtUtils.verity(token);
        if(tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            SaleQuery query = JSON.parseObject(data, SaleQuery.class);
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if(loginInfoDO.getRole() == RoleEnum.ADMIN.getRole()){
                query.setPurchaseId(userId);
            }else {
                query.setFarmerId(userId);
            }
            int total = saleService.total(query);
            return Result.success(total);
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping(value = "api/getSaleDetail")
    public Result getDetail(@RequestParam("data") String saleId){
        List<SaleSubDTO> saleSubDTOS = saleService.getDetail(saleId);
        if(saleSubDTOS != null && !CollectionUtils.isEmpty(saleSubDTOS)){
            List<SaleSubVO> saleSubVOS = saleSubDTOS.stream().map(saleSubDTO -> {
                SaleSubVO saleSubVO  = new SaleSubVO();
                BeanUtils.copyProperties(saleSubDTO, saleSubVO);
                return saleSubVO;
            }).collect(Collectors.toList());
            return Result.success(saleSubVOS);
        }
        return Result.success();
    }

    @PostMapping(value = "api/createSale")
    public Result create(@RequestParam("token") String token, @RequestParam("data") String data){
        String tokenValue = JwtUtils.verity(token);
        if(tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            SaleParam saleParam = JSON.parseObject(data, SaleParam.class);
            SaleDTO saleDTO = new SaleDTO();
            BeanUtils.copyProperties(saleParam, saleDTO);
            String saleId = snowflake.nextIdStr();
            saleDTO.setSaleId(saleId);
            saleDTO.setPurchaseId(userId);
            saleDTO.setStatus(SaleStatus.CREATE.getCode());
            List<SaleSubDTO> saleSubDTOS = saleParam.getItemList().stream().map(saleSubParam -> {
                SaleSubDTO saleSubDTO = new SaleSubDTO();
                BeanUtils.copyProperties(saleSubParam, saleSubDTO);
                saleSubDTO.setSaleId(saleId);
                return saleSubDTO;
            }).collect(Collectors.toList());
            //发送relation创建信息
            RelationDTO relationDTO = new RelationDTO();
            relationDTO.setReplenishmentId(saleParam.getReplenishmentId());
            relationDTO.setSaleId(saleDTO.getSaleId());
            Message<String> msgs = MessageBuilder.withPayload(JSON.toJSONString(relationDTO)).build();
            SendResult sendResult = rocketMQService.syncSend(topic.concat(":CREATE"), msgs);
            if(sendResult.getSendStatus() == SendStatus.SEND_OK){
                if(saleService.create(saleDTO, saleSubDTOS)){
                    return Result.success();
                }
            }
            return Result.error("创建采购单失败");
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping(value = "api/cancelSale")
    public Result cancel(@RequestParam("token") String token, @RequestParam("data") String saleId) {
        String tokenValue = JwtUtils.verity(token);
        if(tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            SaleQuery query = new SaleQuery();
            query.setSaleId(saleId);
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if(loginInfoDO.getRole() == RoleEnum.ADMIN.getRole()){
                query.setPurchaseId(userId);
            }else {
                query.setFarmerId(userId);
            }
            List<SaleDTO> saleDTOS = saleService.getList(query);
            if(saleDTOS != null && !CollectionUtils.isEmpty(saleDTOS)){
                SaleDTO saleDTO = saleDTOS.get(0);
                if(saleDTO.getStatus() == SaleStatus.CANCEL.getCode()){
                    return Result.success();
                }
                if(saleDTO.getStatus() == SaleStatus.CONFIRM.getCode() || saleDTO.getStatus() == SaleStatus.BOOKING.getCode()){
                    return Result.error("采购单已生效，无法取消");
                }
                saleDTO.setStatus(SaleStatus.CANCEL.getCode());
                if(saleService.updateStatus(saleDTO)){
                    return Result.success();
                } else {
                    return Result.error("取消失败");
                }
            } else {
                return Result.error("未查询到采购单");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping(value = "api/confirmSale")
    public Result confirmSale(@RequestParam("token") String token, @RequestParam("data") String saleId) {
        String tokenValue = JwtUtils.verity(token);
        if(tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            SaleQuery query = new SaleQuery();
            query.setSaleId(saleId);
            query.setFarmerId(userId);
            List<SaleDTO> saleDTOS = saleService.getList(query);
            if(saleDTOS != null && !CollectionUtils.isEmpty(saleDTOS)){
                SaleDTO saleDTO = saleDTOS.get(0);
                if(saleDTO.getStatus() == SaleStatus.CONFIRM.getCode() || saleDTO.getStatus() == SaleStatus.BOOKING.getCode()){
                    return Result.success();
                }
                if(saleDTO.getStatus() == SaleStatus.CANCEL.getCode()){
                    return Result.error("采购单已取消，无法确认");
                }
                saleDTO.setStatus(SaleStatus.CONFIRM.getCode());
                if(saleService.updateStatus(saleDTO)){
                    return Result.success();
                } else {
                    return Result.error("确认失败");
                }
            } else {
                return Result.error("未查询到采购单");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping(value = "api/updateSale")
    public Result update(@RequestBody SaleParam param) {
        SaleQuery query = new SaleQuery();
        query.setSaleId(param.getSaleId());
        List<SaleDTO> saleDTOS = saleService.getList(query);
        if(saleDTOS == null || CollectionUtils.isEmpty(saleDTOS)){
            return Result.error("采购单不存在");
        }
        if(saleDTOS.get(0).getStatus() == SaleStatus.CONFIRM.getCode()||saleDTOS.get(0).getStatus() == SaleStatus.BOOKING.getCode()){
            return Result.error("供应商已确认采购单，无法更新");
        }

        //筛选需要删除、创建、更新的记录
        List<SaleSubDTO> detail = saleService.getDetail(param.getSaleId());
        List<SaleSubDTO> create = new ArrayList<>();
        List<SaleSubDTO> update = new ArrayList<>();
        List<SaleSubDTO> delete = new ArrayList<>();
        if(param.getItemList() != null && !CollectionUtils.isEmpty(param.getItemList())){
            List<SaleSubDTO> input = param.getItemList().stream().map(subParam -> {
                SaleSubDTO saleSubDTO = new SaleSubDTO();
                BeanUtils.copyProperties(subParam, saleSubDTO);
                saleSubDTO.setSaleId(param.getSaleId());
                return saleSubDTO;
            }).collect(Collectors.toList());
            for(SaleSubDTO subDTO : input){
                boolean tag = true;
                for(SaleSubDTO exits : detail){
                    if(subDTO.getItemId().equals(exits.getItemId())){
                        if(subDTO.getQuantity().compareTo(exits.getQuantity()) != 0){
                            update.add(subDTO);
                        }
                        tag = false;
                        break;
                    }
                }
                if(tag){
                    create.add(subDTO);
                }
            }
            List<String> itemList = input.stream().map(SaleSubDTO::getItemId).collect(Collectors.toList());
            for(SaleSubDTO subDTO : detail){
                if(!itemList.contains(subDTO.getItemId())){
                    delete.add(subDTO);
                }
            }
        } else {
            delete = detail;
        }
        SaleDTO saleDTO = new SaleDTO();
        BeanUtils.copyProperties(param, saleDTO);
        saleDTO.setStatus(SaleStatus.CREATE.getCode());
        if(saleService.update(saleDTO, update, create, delete)){
            return Result.success();
        } else {
            return Result.error("更新采购单失败");
        }
    }
}
