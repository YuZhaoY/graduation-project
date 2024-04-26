package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.RedisLock;
import com.scu.stu.common.Result;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.common.pay.PayStatus;
import com.scu.stu.common.pay.PayType;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.UserInfoDO;
import com.scu.stu.pojo.DO.queryParam.PayQuery;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DTO.*;
import com.scu.stu.pojo.DTO.message.SaleMessage;
import com.scu.stu.pojo.VO.PayDetailVO;
import com.scu.stu.pojo.VO.PayVO;
import com.scu.stu.service.*;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class PayController {

    @Autowired
    private RedisLock redisLock;

    @Resource
    private PayService payService;

    @Resource
    private UserService userService;

    @Resource
    private RelationService relationService;

    @Resource
    private InboundService inboundService;

    @Resource
    private RefundService refundService;

    @Resource
    private SaleService saleService;

    @Resource(name = "rocketMQService")
    private RocketMQTemplate rocketMQService;

    @Value("${rocketmq.topic.sale}")
    private String sale;

    private final String tokenCheck = "token";

    private final long expireTime = 30*60*1000; //30分钟

    @GetMapping("api/getPayList")
    public Result getList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            PayQuery query = JSON.parseObject(data, PayQuery.class);
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            boolean admin = true;
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                admin = false;
                query.setFarmerId(userId);
            } else {
                query.setInspectorId(userId);
            }
            List<PayDTO> payDTOS = payService.query(query);
            if (payDTOS != null && !CollectionUtils.isEmpty(payDTOS)) {
                List<UserInfoDO> userInfoDOS = new ArrayList<>();
                if(admin){
                    List<String> userList = payDTOS.stream().map(PayDTO::getFarmerId).collect(Collectors.toList());
                    userInfoDOS = userService.batchQueryUserInfo(userList);
                }
                boolean finalAdmin = admin;
                List<UserInfoDO> finalUserInfoDOS = userInfoDOS;
                List<PayVO> payVOS = payDTOS.stream().map(payDTO -> {
                    PayVO payVO = new PayVO();
                    BeanUtils.copyProperties(payDTO, payVO);
                    payVO.setGMTCreate(DateUtils.format(payDTO.getGMTCreate()));
                    payVO.setStatus(PayStatus.getDescByCode(payDTO.getStatus()));
                    payVO.setType(PayType.getDescByCode(payDTO.getType()));
                    if(finalAdmin && payDTO.getType() == PayType.REFUND.getCode()){
                        payVO.setQRCode(getQRCode(finalUserInfoDOS, payDTO.getFarmerId()));
                    }
                    return payVO;
                }).collect(Collectors.toList());
                return Result.success(payVOS);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/getPayTotal")
    public Result total(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            PayQuery query = JSON.parseObject(data, PayQuery.class);
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                query.setFarmerId(userId);
            } else {
                query.setInspectorId(userId);
            }

            return Result.success(payService.total(query));
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping("api/pay")
    public Result pay(@RequestParam(value = "token") String token, @RequestParam(value = "data") String payId) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            PayQuery query = new PayQuery();
            query.setPayId(payId);
            List<PayDTO> payDTOS = payService.query(query);
            if (payDTOS != null && !CollectionUtils.isEmpty(payDTOS)) {
                PayDTO payDTO = payDTOS.get(0);
                payDTO.setStatus(PayStatus.PAY.getCode());

                //入库付款类型（Inbound）——更新采购单
                if (payDTO.getType() == PayType.INBOUND.getCode()) {
                    RelationQuery relationQuery = new RelationQuery();
                    relationQuery.setPayId(payId);
                    List<RelationDTO> relationDTOS = relationService.query(relationQuery);
                    if (CollectionUtils.isEmpty(payDTOS)) {
                        return Result.error("未查询到采购单");
                    }
                    SaleMessage message = new SaleMessage();
                    message.setSaleId(relationDTOS.get(0).getSaleId());
                    Message<String> msgs = MessageBuilder.withPayload(JSON.toJSONString(message)).build();
                    SendResult sendResult = rocketMQService.syncSend(sale.concat(":PAY"), msgs);
                    if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                        return Result.error("采购单更新失败");
                    }
                }
                if (payService.update(payDTO)) {
                    return Result.success();
                } else {
                    return Result.error("付款单确认失败");
                }
            } else {
                return Result.error("未查到付款单");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }
    @PostMapping("api/getDetail")
    public Result detail(@RequestParam(value = "token") String token, @RequestParam(value = "data") String payId) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            PayQuery query = new PayQuery();
            query.setPayId(payId);
            List<PayDTO> payDTOS = payService.query(query);
            if (payDTOS != null && !CollectionUtils.isEmpty(payDTOS)) {
                PayDTO payDTO = payDTOS.get(0);
                RelationQuery relationQuery = new RelationQuery();
                if(payDTO.getType() == PayType.INBOUND.getCode()){
                    relationQuery.setPayId(payId);
                }else {
                    relationQuery.setReturnId(payId);
                }
                List<RelationDTO> relationDTOS = relationService.query(relationQuery);
                RelationDTO relationDTO = relationDTOS.get(0);
                List<PayDetailVO> detailVOS = new ArrayList<>();
                List<SaleSubDTO> saleSubDTOS = saleService.getDetail(relationDTO.getSaleId());

                if(payDTO.getType() == PayType.INBOUND.getCode()){
                    List<InboundSubDTO> inboundSubDTOS = inboundService.detail(relationDTO.getInboundId());

                    detailVOS = inboundSubDTOS.stream().map(detail ->{
                        PayDetailVO payDetailVO = new PayDetailVO();
                        payDetailVO.setItemId(detail.getItemId());
                        payDetailVO.setQuantity(detail.getQuantity());
                        for(SaleSubDTO saleSubDTO: saleSubDTOS){
                            if(detail.getItemId().equals(saleSubDTO.getItemId())){
                                payDetailVO.setPrice(saleSubDTO.getPrice());
                                break;
                            }
                        }
                        return payDetailVO;
                    }).collect(Collectors.toList());
                } else {
                    List<RefundSubDTO> refundSubDTOS = refundService.detail(relationDTO.getRefundId());
                    detailVOS = refundSubDTOS.stream().map(detail ->{
                        PayDetailVO payDetailVO = new PayDetailVO();
                        payDetailVO.setItemId(detail.getItemId());
                        payDetailVO.setQuantity(detail.getQuantity());
                        for(SaleSubDTO saleSubDTO: saleSubDTOS){
                            if(detail.getItemId().equals(saleSubDTO.getItemId())){
                                payDetailVO.setPrice(saleSubDTO.getPrice());
                                break;
                            }
                        }
                        return payDetailVO;
                    }).collect(Collectors.toList());
                }
                return Result.success(detailVOS);

            } else {
                return Result.error("未查到付款单");
            }
        }else {
            return Result.error("未查到身份信息");
        }
    }

    public String getQRCode(List<UserInfoDO> userInfoDOS, String userId){
        for(UserInfoDO userInfoDO: userInfoDOS){
            if(userId.equals(userInfoDO.getUserId())){
                return userInfoDO.getQRCode();
            }
        }
        return null;
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
