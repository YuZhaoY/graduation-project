package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.Result;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.queryParam.InboundQuery;
import com.scu.stu.pojo.DTO.InboundDTO;
import com.scu.stu.pojo.DTO.InboundSubDTO;
import com.scu.stu.pojo.VO.InboundSubVO;
import com.scu.stu.pojo.VO.InboundVO;
import com.scu.stu.pojo.VO.param.InboundParam;
import com.scu.stu.service.InboundService;
import com.scu.stu.service.UserService;
import com.scu.stu.utils.DateUtils;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import wiki.xsx.core.snowflake.config.Snowflake;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class InboundController {

    @Autowired
    private Snowflake snowflake;

    @Resource
    private UserService userService;

    @Resource
    private InboundService inboundService;

    @GetMapping("api/getInboundList")
    public Result getList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            InboundQuery query = JSON.parseObject(data, InboundQuery.class);
            List<InboundDTO> inboundDTOList = inboundService.query(query);
            if(inboundDTOList != null && !CollectionUtils.isEmpty(inboundDTOList)){
                List<InboundVO> inboundVOList = inboundDTOList.stream().map(inboundDTO -> {
                    InboundVO inboundVO = new InboundVO();
                    BeanUtils.copyProperties(inboundDTO, inboundVO);
                    inboundVO.setGMTCreate(DateUtils.format(inboundDTO.getGMTCreate()));
                    inboundVO.setInboundTime(DateUtils.format(inboundDTO.getInboundTime()));
                    return inboundVO;
                }).collect(Collectors.toList());
                return Result.success(inboundVOList);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }

    }

    @GetMapping("api/getInboundTotal")
    public Result total(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            InboundQuery query = JSON.parseObject(data, InboundQuery.class);
            return Result.success(inboundService.total(query));
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/getInboundDetail")
    public Result detail(@RequestParam(value = "token") String token, @RequestParam(value = "data") String inboundId){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            List<InboundSubDTO> inboundSubDTOS = inboundService.detail(inboundId);
            if(inboundSubDTOS != null && !CollectionUtils.isEmpty(inboundSubDTOS)){
                List<InboundSubVO> inboundSubVOList = inboundSubDTOS.stream().map(inboundSubDTO -> {
                    InboundSubVO inboundSubVO = new InboundSubVO();
                    BeanUtils.copyProperties(inboundSubDTO, inboundSubVO);
                    return inboundSubVO;
                }).collect(Collectors.toList());
                return Result.success(inboundSubVOList);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }

    }

    @PutMapping("api/createInbound")
    public Result create(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                return Result.error("供应商不能创建入库单，请联系仓库小二创建");
            }
            InboundParam param = JSON.parseObject(data, InboundParam.class);
            InboundDTO inboundDTO = new InboundDTO();
            String inboundId = snowflake.nextIdStr();
            BeanUtils.copyProperties(param, inboundDTO);
            inboundDTO.setInboundId(inboundId);
            inboundDTO.setInspectorId(userId);
            if(param.getItemList() != null && !CollectionUtils.isEmpty(param.getItemList())){
                List<InboundSubDTO> inboundSubDTOList = param.getItemList().stream().map(inboundSubParam -> {
                    InboundSubDTO inboundSubDTO = new InboundSubDTO();
                    BeanUtils.copyProperties(inboundSubParam, inboundSubDTO);
                    inboundSubDTO.setInboundId(inboundId);
                    return inboundSubDTO;
                }).collect(Collectors.toList());
                return inboundService.create(inboundDTO, inboundSubDTOList, param.getBookingId());
            } else {
                return Result.error("入库货品列表不能为空");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }
}
