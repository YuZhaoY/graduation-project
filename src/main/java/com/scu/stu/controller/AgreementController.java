package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.AgreementStatus;
import com.scu.stu.common.Result;
import com.scu.stu.pojo.DO.AgreementDO;
import com.scu.stu.pojo.DO.UserInfoDO;
import com.scu.stu.pojo.DTO.AgreementDTO;
import com.scu.stu.pojo.VO.param.AgreementParam;
import com.scu.stu.pojo.VO.AgreementVO;
import com.scu.stu.service.AgreementService;
import com.scu.stu.service.UserService;
import com.scu.stu.utils.DateUtils;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import wiki.xsx.core.snowflake.config.Snowflake;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class AgreementController {

    @Resource
    private AgreementService agreementService;

    @Autowired
    private Snowflake snowflake;

    @PostMapping(value = "api/saveAgreement")
    public Result saveAgreement(@RequestParam("data") String data, @RequestParam("token") String token) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            AgreementParam agreementParam = JSON.parseObject(data, AgreementParam.class);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            AgreementDTO agreementDTO = new AgreementDTO();
            agreementDTO.setFarmerId(agreementParam.getFarmerId());
            agreementDTO.setContent(agreementParam.getAvatar());
            agreementDTO.setEffectStartTime(agreementParam.getEffectTime().get(0));
            agreementDTO.setEffectEndTime(agreementParam.getEffectTime().get(1));
            agreementDTO.setGMTCreate(df.format(new Date()));
            agreementDTO.setPurchaseId(userId);
            agreementDTO.setStatus(AgreementStatus.CREATE.getCode());
            agreementDTO.setVersion(0);
            agreementDTO.setAgreementId(snowflake.nextIdStr());
            AgreementDO agreementDO = new AgreementDO();
            BeanUtils.copyProperties(agreementDTO,agreementDO);
            boolean insertRes = agreementService.insert(agreementDO);
            if(insertRes){
                return Result.success();
            } else{
                return Result.error("创建供货协议失败");
            }

        } else {
            return Result.error("未查到身份信息");
        }
    }
    @GetMapping(value = "api/getAgreementList")
    public Result getAgreementList(@RequestParam("token") String token, @RequestParam("pageNow") int pageNow, @RequestParam("pageSize") int pageSize) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            List<AgreementDTO> agreementDTOList = agreementService.queryAgreementList(userId, pageNow, pageSize);
            if(agreementDTOList.isEmpty()){
                return Result.success();
            }
            List<AgreementVO> agreementVOList = agreementDTOList.stream().map(agreementDTO -> {
                AgreementVO agreementVO = new AgreementVO();
                BeanUtils.copyProperties(agreementDTO, agreementVO);
                agreementVO.setStatus(AgreementStatus.getDescByCode(agreementDTO.getStatus()));
                return agreementVO;
            }).collect(Collectors.toList());
            return Result.success(agreementVOList);
        } else {
            return Result.error("未查到身份信息");
        }
    }
    @GetMapping(value = "api/admin/countAgreement")
    public Result countAgreement(@RequestParam("token") String token) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            int count = agreementService.countByPurchaseId(userId);
            return Result.success(count);
        } else {
            return Result.error("未查到身份信息");
        }
    }
    @PutMapping(value = "api/updateAgreementStatus")
    public Result updateAgreementStatus(@RequestParam("agreementId") String agreementId, @RequestParam("status") String status){
        int newStatus=AgreementStatus.getCodeByDesc(status);
        AgreementDTO agreement = agreementService.query(agreementId);
        if(agreement.getStatus() == AgreementStatus.SIGN.getCode()){
            return Result.error("协议已经生效，无法修改");
        } else if(agreement.getStatus() == AgreementStatus.CANCEL.getCode()){
            if(newStatus == AgreementStatus.CANCEL.getCode() || newStatus == AgreementStatus.SIGN.getCode()){
                return Result.success();//幂等
            }
        }
        AgreementDTO  agreementDTO = new AgreementDTO();
        if(newStatus == AgreementStatus.CANCEL.getCode()){
            agreementDTO.setAgreementId(agreementId);
            agreementDTO.setStatus(newStatus);
            agreementDTO.setGMTModified(DateUtils.format(new Date()));
        } else {
            agreementDTO.setAgreementId(agreementId);
            agreementDTO.setStatus(newStatus);
            agreementDTO.setGMTModified(DateUtils.format(new Date()));
            agreementDTO.setGMTSign(DateUtils.format(new Date()));
        }

        if(agreementService.updateAgreement(agreementDTO)){
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }
    @PostMapping(value = "api/updateAgreement")
    public Result updateAgreement(@RequestParam("data") String data) {
        AgreementParam agreementParam = JSON.parseObject(data, AgreementParam.class);
        AgreementDTO agreement = agreementService.query(agreementParam.getAgreementId());
        if(agreement.getStatus() == AgreementStatus.SIGN.getCode()){
            return Result.error("协议生效中，无法修改");
        }
        AgreementDTO agreementDTO = new AgreementDTO();
        agreementDTO.setAgreementId(agreementParam.getAgreementId());
        agreementDTO.setGMTModified(DateUtils.format(new Date()));
        agreementDTO.setFarmerId(agreementParam.getFarmerId());
        agreementDTO.setContent(agreementParam.getAvatar());
        agreementDTO.setEffectStartTime(agreementParam.getEffectTime().get(0));
        agreementDTO.setEffectEndTime(agreementParam.getEffectTime().get(1));
        agreementDTO.setStatus(AgreementStatus.CREATE.getCode());
        boolean result = agreementService.updateAgreement(agreementDTO);
        if (result) {
            return Result.success();
        } else {
            return Result.error("更新供货协议失败");
        }

    }
}
