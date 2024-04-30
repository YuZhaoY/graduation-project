package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.RedisLock;
import com.scu.stu.common.Result;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.common.pay.PayType;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.queryParam.BookingQuery;
import com.scu.stu.pojo.DO.queryParam.RefundQuery;
import com.scu.stu.pojo.DTO.*;
import com.scu.stu.pojo.DTO.message.Item;
import com.scu.stu.pojo.DTO.message.PayMessage;
import com.scu.stu.pojo.VO.RefundSubVO;
import com.scu.stu.pojo.VO.RefundVO;
import com.scu.stu.pojo.VO.param.RefundParam;
import com.scu.stu.service.BookingService;
import com.scu.stu.service.RefundService;
import com.scu.stu.service.RelationService;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class RefundController {

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private Snowflake snowflake;

    @Resource
    private UserService userService;

    @Resource
    private BookingService bookingService;

    @Resource
    private RefundService refundService;

    @Resource
    private RelationService relationService;

    @Resource(name = "rocketMQService")
    private RocketMQTemplate rocketMQService;

    @Value("${rocketmq.topic.pay}")
    private String topic;

    @Value("${rocketmq.topic.relation}")
    private String relation;

    private final String tokenCheck = "token";

    private final long expireTime = 30*60*1000; //30分钟

    @GetMapping("api/admin/getRefundList")
    public Result getList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
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

    @GetMapping("api/editor/getRefundList")
    public Result getEditorList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            RefundQuery query = JSON.parseObject(data, RefundQuery.class);

            //查询当前供应商下预约单
            BookingQuery bookingQuery =new BookingQuery();
            bookingQuery.setFarmerId(userId);
            List<BookingDTO> bookingDTOList = bookingService.queryNoPage(bookingQuery);

            if(bookingDTOList != null && !CollectionUtils.isEmpty(bookingDTOList)){
                List<String> bookingIdList = bookingDTOList.stream().map(BookingDTO::getBookingId).collect(Collectors.toList());
                //查询关系表,获取退供单ID列表
                List<RelationDTO> relationDTOS = relationService.queryByBookingList(bookingIdList);
                if (relationDTOS != null && !CollectionUtils.isEmpty(relationDTOS)) {
                    List<String> refundIdList = relationDTOS.stream().map(RelationDTO::getRefundId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(refundIdList)) {
                        //查询退供单列表
                        List<RefundDTO> refundDTOList = refundService.batchQuery(refundIdList, query);
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
                    }
                }
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }

    }

    @GetMapping("api/admin/getRefundTotal")
    public Result total(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            RefundQuery query = JSON.parseObject(data, RefundQuery.class);
            return Result.success(refundService.total(query));
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/editor/getRefundTotal")
    public Result editorTotal(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            RefundQuery query = JSON.parseObject(data, RefundQuery.class);

            //查询当前供应商下预约单
            BookingQuery bookingQuery =new BookingQuery();
            bookingQuery.setFarmerId(userId);
            List<BookingDTO> bookingDTOList = bookingService.queryNoPage(bookingQuery);

            if(bookingDTOList != null && !CollectionUtils.isEmpty(bookingDTOList)){
                List<String> bookingIdList = bookingDTOList.stream().map(BookingDTO::getBookingId).collect(Collectors.toList());
                //查询关系表,获取退供单ID列表
                List<RelationDTO> relationDTOS = relationService.queryByBookingList(bookingIdList);
                if (relationDTOS != null && !CollectionUtils.isEmpty(relationDTOS)) {
                    List<String> refundIdList = relationDTOS.stream().map(RelationDTO::getRefundId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(refundIdList)) {
                        //查询退供单列表
                        List<RefundDTO> refundDTOList = refundService.batchQuery(refundIdList, query);
                        if(refundDTOList != null && !CollectionUtils.isEmpty(refundDTOList)) {
                            return Result.success(refundDTOList.size());
                        }
                    }
                }
            }
            return Result.success(0);
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/getRefundDetail")
    public Result detail(@RequestParam(value = "token") String token, @RequestParam(value = "data") String refundId){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
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
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                return Result.error("供应商不能创建退供单，请联系仓库小二创建");
            }
            RefundParam param = JSON.parseObject(data, RefundParam.class);
            BookingQuery bookingQuery = new BookingQuery();
            bookingQuery.setBookingId(param.getBookingId());
            List<BookingDTO> bookingDTOS = bookingService.query(bookingQuery);
            if(bookingDTOS == null || CollectionUtils.isEmpty(bookingDTOS)){
                return Result.error("预约单无效，请输入正确的预约单ID");
            }

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

                //发送payOrder创建消息
                PayMessage message = new PayMessage();
                message.setBookingId(param.getBookingId());
                message.setInspectorId(userId);
                message.setType(PayType.REFUND.getCode());// 2:退供
                List<Item> itemList = param.getItemList().stream().map(paramItem -> {
                    Item item = new Item();
                    BeanUtils.copyProperties(paramItem, item);
                    return item;
                }).collect(Collectors.toList());
                message.setItemList(itemList);
                Message<String> msgs = MessageBuilder.withPayload(JSON.toJSONString(message)).build();
                SendResult sendResult = rocketMQService.syncSend(topic, msgs);

                //发送relation更新信息
                RelationDTO relationDTO = new RelationDTO();
                relationDTO.setBookingId(param.getBookingId());
                relationDTO.setRefundId(refundId);
                msgs = MessageBuilder.withPayload(JSON.toJSONString(relationDTO)).build();
                SendResult sendResult2 = rocketMQService.syncSend(relation.concat(":REFUND"), msgs);
                if(sendResult.getSendStatus() == SendStatus.SEND_OK && sendResult2.getSendStatus() == SendStatus.SEND_OK){
                    if(refundService.create(refundDTO, refundSubDTOList)){
                        return Result.success();
                    }
                }
                return Result.error("创建付款单失败");
            } else {
                return Result.error("退供货品列表不能为空");
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
