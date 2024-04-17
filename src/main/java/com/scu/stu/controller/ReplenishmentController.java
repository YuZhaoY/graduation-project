package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.ReplenishmentStatus;
import com.scu.stu.common.Result;
import com.scu.stu.pojo.DO.queryParam.ReplenishmentQuery;
import com.scu.stu.pojo.DO.queryParam.ReplenishmentSubQuery;
import com.scu.stu.pojo.DTO.ReplenishmentDTO;
import com.scu.stu.pojo.DTO.ReplenishmentSubDTO;
import com.scu.stu.pojo.VO.ReplenishmentSubVO;
import com.scu.stu.pojo.VO.ReplenishmentVO;
import com.scu.stu.pojo.VO.param.ReplenishmentParam;
import com.scu.stu.pojo.VO.param.ReplenishmentSubParam;
import com.scu.stu.service.ReplenishmentService;
import com.scu.stu.utils.DateUtils;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import wiki.xsx.core.snowflake.config.Snowflake;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class ReplenishmentController {

    @Resource
    private ReplenishmentService replenishmentService;

    @Autowired
    private Snowflake snowflake;

    @PostMapping(value = "api/getReplenishmentList")
    public Result getReplenishmentList(@RequestParam("token") String token, @RequestParam("data") String data){
        String tokenValue = JwtUtils.verity(token);
        if(tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            ReplenishmentQuery query = JSON.parseObject(data, ReplenishmentQuery.class);
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            query.setPurchaseId(userId);
            List<ReplenishmentDTO> replenishmentDTOS = replenishmentService.query(query);
            if(!CollectionUtils.isEmpty(replenishmentDTOS)){
                List<ReplenishmentVO> replenishmentVOS = replenishmentDTOS.stream().map(replenishmentDTO -> {
                    ReplenishmentVO replenishmentVO = new ReplenishmentVO();
                    BeanUtils.copyProperties(replenishmentDTO, replenishmentVO);
                    replenishmentVO.setGMTCreate(DateUtils.format(replenishmentDTO.getGMTCreate()));
                    return replenishmentVO;
                }).collect(Collectors.toList());

                return Result.success(replenishmentVOS);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping(value = "api/getTotal")
    public Result getTotal(@RequestParam("token") String token, @RequestParam("data") String data){
        String tokenValue = JwtUtils.verity(token);
        if(tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            ReplenishmentQuery query = JSON.parseObject(data, ReplenishmentQuery.class);
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            query.setPurchaseId(userId);
            int total = replenishmentService.total(query);
            return Result.success(total);
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping(value = "api/getReplenishmentDetail")
    public Result querySub(@RequestParam("replenishmentId") String replenishmentId){
        if(replenishmentId == null || "".equals(replenishmentId)){
            return Result.error("补货计划ID为空");
        }
        ReplenishmentSubQuery query = new ReplenishmentSubQuery();
        query.setReplenishmentId(replenishmentId);
        List<ReplenishmentSubDTO> replenishmentSubDTOS = replenishmentService.querySub(query);
        if(!CollectionUtils.isEmpty(replenishmentSubDTOS)){
            List<ReplenishmentSubVO> replenishmentSubVOS = replenishmentSubDTOS.stream().map(replenishmentSubDTO -> {
                ReplenishmentSubVO replenishmentSubVO = new ReplenishmentSubVO();
                BeanUtils.copyProperties(replenishmentSubDTO, replenishmentSubVO);
                return replenishmentSubVO;
            }).collect(Collectors.toList());
            return Result.success(replenishmentSubVOS);
        }
        return Result.success();
    }

    @GetMapping(value = "api/cancelReplenishment")
    public Result cancelReplenishment(@RequestParam("replenishmentId") String replenishmentId) throws ParseException {
        if(replenishmentId == null || "".equals(replenishmentId)){
            return Result.error("补货计划ID为空");
        }
        ReplenishmentQuery query = new ReplenishmentQuery();
        query.setReplenishmentId(replenishmentId);
        List<ReplenishmentDTO> replenishmentDTOS = replenishmentService.query(query);
        if(replenishmentDTOS == null || CollectionUtils.isEmpty(replenishmentDTOS)){
            return Result.error("未查到补货计划，请创建");
        }
        if(ReplenishmentStatus.CANCEL.getDesc().equals(replenishmentDTOS.get(0).getStatus())){
            return Result.success();
        }
        if(ReplenishmentStatus.VALID.getDesc().equals(replenishmentDTOS.get(0).getStatus())){
            return Result.error("补货计划已生效，无法取消");
        }
        ReplenishmentDTO replenishmentDTO = new ReplenishmentDTO();
        replenishmentDTO.setReplenishmentId(replenishmentId);
        replenishmentDTO.setStatus(ReplenishmentStatus.CANCEL.getDesc());
        if(replenishmentService.updateReplenishment(replenishmentDTO)){
            return Result.success();
        } else {
            return Result.error("取消补货计划失败");
        }
    }

    @PostMapping(value = "api/createReplenishment")
    public Result create(@RequestParam("data") String data, @RequestParam("token") String token) throws ParseException {
        String tokenValue = JwtUtils.verity(token);
        if(tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            ReplenishmentParam replenishmentParam = JSON.parseObject(data, ReplenishmentParam.class);
            if(replenishmentParam.getItemList() == null || CollectionUtils.isEmpty(replenishmentParam.getItemList())) {
                return Result.error("货品为空，请重新上传");
            }
            if(!isValid(null, replenishmentParam)){
                return Result.error("存在生效中的货品，请重新选择");
            }
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            ReplenishmentDTO replenishmentDTO = new ReplenishmentDTO();
            replenishmentDTO.setReplenishmentId(snowflake.nextIdStr());
            replenishmentDTO.setPurchaseId(userId);
            replenishmentDTO.setFarmerId(replenishmentParam.getFarmerId());
            replenishmentDTO.setExpiration(replenishmentParam.getExpiration());
            replenishmentDTO.setStartExpiration(replenishmentParam.getStartExpiration());
            replenishmentDTO.setStatus(ReplenishmentStatus.CREATE.getDesc());
            replenishmentDTO.setVersion(0);
            List<ReplenishmentSubDTO> replenishmentSubDTOS = replenishmentParam.getItemList().stream().map(item -> {
                ReplenishmentSubDTO subDTO = new ReplenishmentSubDTO();
                subDTO.setItemId(item.getItemId());
                subDTO.setQuantity(item.getQuantity());
                subDTO.setReplenishmentId(replenishmentDTO.getReplenishmentId());
                return subDTO;
            }).collect(Collectors.toList());
            if(replenishmentService.createReplenishment(replenishmentDTO, replenishmentSubDTOS)){
                return Result.success();
            } else {
                return Result.error("创建补货计划失败");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping(value = "api/updateReplenishment")
    public Result update(@RequestBody ReplenishmentParam param) throws ParseException {
        ReplenishmentQuery query = new ReplenishmentQuery();
        query.setReplenishmentId(param.getReplenishmentId());
        List<ReplenishmentDTO> replenishmentDTOS = replenishmentService.query(query);
        if(replenishmentDTOS == null || CollectionUtils.isEmpty(replenishmentDTOS)){
            return Result.error("补货计划不存在");
        }
        if(ReplenishmentStatus.INVALID.getDesc().equals(replenishmentDTOS.get(0).getStatus())){
            return Result.error("补货计划已过期，无法更新");
        }
        if(ReplenishmentStatus.VALID.getDesc().equals(replenishmentDTOS.get(0).getStatus())){
            return Result.error("补货计划生效中，无法更新");
        }
        if(!isValid(param.getReplenishmentId(), param)){
            return Result.error("存在生效中的货品，请重新选择");
        }

        //筛选需要删除、创建、更新的记录
        ReplenishmentSubQuery subQuery = new ReplenishmentSubQuery();
        subQuery.setReplenishmentId(param.getReplenishmentId());
        List<ReplenishmentSubDTO> replenishmentSubDTOS = replenishmentService.querySub(subQuery);
        List<ReplenishmentSubDTO> create = new ArrayList<>();
        List<ReplenishmentSubDTO> update = new ArrayList<>();
        List<ReplenishmentSubDTO> delete = new ArrayList<>();
        if(param.getItemList() != null && !CollectionUtils.isEmpty(param.getItemList())){
            List<ReplenishmentSubDTO> input = param.getItemList().stream().map(subParam -> {
                ReplenishmentSubDTO replenishmentSubDTO = new ReplenishmentSubDTO();
                replenishmentSubDTO.setReplenishmentId(param.getReplenishmentId());
                replenishmentSubDTO.setQuantity(subParam.getQuantity());
                replenishmentSubDTO.setItemId(subParam.getItemId());
                return replenishmentSubDTO;
            }).collect(Collectors.toList());
            for(ReplenishmentSubDTO subDTO : input){
                boolean tag = true;
                for(ReplenishmentSubDTO exits : replenishmentSubDTOS){
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
            List<String> itemList = input.stream().map(ReplenishmentSubDTO::getItemId).collect(Collectors.toList());
            for(ReplenishmentSubDTO subDTO : replenishmentSubDTOS){
                if(!itemList.contains(subDTO.getItemId())){
                    delete.add(subDTO);
                }
            }
        } else {
            delete = replenishmentSubDTOS;
        }
        ReplenishmentDTO replenishmentDTO = new ReplenishmentDTO();
        replenishmentDTO.setReplenishmentId(param.getReplenishmentId());
        replenishmentDTO.setStatus(ReplenishmentStatus.CREATE.getDesc());
        replenishmentDTO.setExpiration(param.getExpiration());
        replenishmentDTO.setStartExpiration(param.getStartExpiration());
        if(replenishmentService.updateReplenishment(replenishmentDTO, update, create, delete)){
            return Result.success();
        } else {
            return Result.error("更新补货计划失败");
        }
    }

    @GetMapping(value = "api/test")
    public Result updateStatus(){
        replenishmentService.updateInvalid();
        replenishmentService.updateValid();
        return Result.success();
    }

    /**
     * 判断是否可以创建补货计划，品是否已经存在于补货计划中
     * @return
     */
    public boolean isValid(String replenishmentId, ReplenishmentParam param) throws ParseException {
        ReplenishmentQuery query = new ReplenishmentQuery();
        query.setStatus(ReplenishmentStatus.VALID.getCode());
        List<ReplenishmentDTO> replenishmentDTOS = replenishmentService.query(query);
        query.setStatus(ReplenishmentStatus.CREATE.getCode());
        List<ReplenishmentDTO> replenishmentDTOS2 = replenishmentService.query(query);
        if(replenishmentDTOS2 == null || CollectionUtils.isEmpty(replenishmentDTOS2)) {
            replenishmentDTOS.addAll(replenishmentDTOS2);
        }
        if(replenishmentDTOS == null || CollectionUtils.isEmpty(replenishmentDTOS)){
            return true;
        }
        //判断有效区间是否重叠
        Date iStart = DateUtils.parse(param.getStartExpiration());
        Date iEnd = DateUtils.parse(param.getExpiration());
        List<ReplenishmentDTO> overlap = new ArrayList<>();
        for(ReplenishmentDTO replenishmentDTO: replenishmentDTOS){
            Date start = DateUtils.parse(replenishmentDTO.getStartExpiration());
            Date end = DateUtils.parse(replenishmentDTO.getExpiration());
            if(iStart.after(end) || iEnd.before(start)){
                continue;
            } else {
                overlap.add(replenishmentDTO);
            }
        }
        if(overlap == null || CollectionUtils.isEmpty(overlap)){
            return true;
        }
        List<String> replenishmentIdList = overlap.stream().map(ReplenishmentDTO::getReplenishmentId).collect(Collectors.toList());
        if(replenishmentId != null && !"".equals(replenishmentId)){
            replenishmentIdList.remove(replenishmentId);
        }
        List<ReplenishmentSubParam> itemList = param.getItemList();
        List<ReplenishmentSubDTO> replenishmentSubDTOS = replenishmentService.batchQuerySub(replenishmentIdList);
        if(!CollectionUtils.isEmpty(replenishmentSubDTOS)) {
            List<String> validItemList = replenishmentSubDTOS.stream().map(ReplenishmentSubDTO::getItemId).collect(Collectors.toList());
            for (ReplenishmentSubParam item : itemList) {
                if (validItemList.contains(item.getItemId())) {
                    return false;
                }
            }
        }
        return true;
    }
}
