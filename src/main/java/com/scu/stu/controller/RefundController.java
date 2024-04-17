package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.Result;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.queryParam.RefundQuery;
import com.scu.stu.pojo.DTO.PayDTO;
import com.scu.stu.pojo.DTO.PayMessage;
import com.scu.stu.pojo.DTO.RefundDTO;
import com.scu.stu.pojo.DTO.RefundSubDTO;
import com.scu.stu.pojo.VO.RefundSubVO;
import com.scu.stu.pojo.VO.RefundVO;
import com.scu.stu.pojo.VO.param.RefundParam;
import com.scu.stu.service.RefundService;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class RefundController {

    @Autowired
    private Snowflake snowflake;

    @Resource
    private UserService userService;

    @Resource
    private RefundService refundService;

    @Resource(name = "rocketMQService")
    private RocketMQTemplate rocketMQService;

    @Value("${rocketmq.topic.pay}")
    private String topic;

    @Value("${rocketmq.tag.pay}")
    private String tag;

    @GetMapping("api/getRefundList")
    public Result getList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            RefundQuery query = JSON.parseObject(data, RefundQuery.class);
            List<RefundDTO> refundDTOList = refundService.query(query);
            if(refundDTOList != null && !CollectionUtils.isEmpty(refundDTOList)){
                List<RefundVO> refundVOList = refundDTOList.stream().map(refundDTO -> {
                    RefundVO refundVO = new RefundVO();
                    BeanUtils.copyProperties(refundDTO, refundVO);
                    refundVO.setGMTCreate(DateUtils.format(refundDTO.getGMTCreate()));
                    refundVO.setRefundTime(DateUtils.format(refundDTO.getRefundTime()));
                    return refundVO;
                }).collect(Collectors.toList());
                return Result.success(refundVOList);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }

    }

    @GetMapping("api/getRefundTotal")
    public Result total(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            RefundQuery query = JSON.parseObject(data, RefundQuery.class);
            return Result.success(refundService.total(query));
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/getRefundDetail")
    public Result detail(@RequestParam(value = "token") String token, @RequestParam(value = "data") String refundId){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            List<RefundSubDTO> refundSubDTOS = refundService.detail(refundId);
            if(refundSubDTOS != null && !CollectionUtils.isEmpty(refundSubDTOS)){
                List<RefundSubVO> refundSubVOList = refundSubDTOS.stream().map(refundSubDTO -> {
                    RefundSubVO refundSubVO = new RefundSubVO();
                    BeanUtils.copyProperties(refundSubDTO, refundSubVO);
                    return refundSubVO;
                }).collect(Collectors.toList());
                return Result.success(refundSubVOList);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }

    }

    @PutMapping("api/createRefund")
    public Result create(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                return Result.error("供应商不能创建退供单，请联系仓库小二创建");
            }
            RefundParam param = JSON.parseObject(data, RefundParam.class);
            RefundDTO refundDTO = new RefundDTO();
            String refundId = snowflake.nextIdStr();
            BeanUtils.copyProperties(param, refundDTO);
            refundDTO.setRefundId(refundId);
            refundDTO.setInspectorId(userId);
            if(param.getItemList() != null && !CollectionUtils.isEmpty(param.getItemList())){
                List<RefundSubDTO> refundSubDTOList = param.getItemList().stream().map(refundSubParam -> {
                    RefundSubDTO refundSubDTO = new RefundSubDTO();
                    BeanUtils.copyProperties(refundSubParam, refundSubDTO);
                    refundSubDTO.setRefundId(refundId);
                    return refundSubDTO;
                }).collect(Collectors.toList());
                PayMessage message = new PayMessage();
                message.setBookingId(param.getBookingId());
                message.setInspectorId(userId);
                message.setType();// 2:退供
                Message<String> msgs = MessageBuilder.withPayload(JSON.toJSONString(message)).build();
                SendResult sendResult = rocketMQService.syncSend(topic, msgs);
                if(sendResult.getSendStatus() == SendStatus.SEND_OK){
                    return refundService.create(refundDTO, refundSubDTOList, param.getBookingId());
                } else {
                    return Result.error("创建付款单失败");
                }
            } else {
                return Result.error("退供货品列表不能为空");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/testSend")
    public Result send(){
        Message<String> msgs = MessageBuilder.withPayload("这是一个测试message"+new Date()).build();
        String destination = topic+":"+tag;
        SendResult sendResult = rocketMQService.syncSend(destination, msgs);
        return Result.success(sendResult.getSendStatus() == SendStatus.SEND_OK);
    }
}
