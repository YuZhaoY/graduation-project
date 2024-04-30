package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.RedisLock;
import com.scu.stu.common.Result;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.common.pay.PayType;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.queryParam.BookingQuery;
import com.scu.stu.pojo.DO.queryParam.InboundQuery;
import com.scu.stu.pojo.DTO.BookingDTO;
import com.scu.stu.pojo.DTO.InboundDTO;
import com.scu.stu.pojo.DTO.InboundSubDTO;
import com.scu.stu.pojo.DTO.RelationDTO;
import com.scu.stu.pojo.DTO.message.Item;
import com.scu.stu.pojo.DTO.message.PayMessage;
import com.scu.stu.pojo.VO.InboundSubVO;
import com.scu.stu.pojo.VO.InboundVO;
import com.scu.stu.pojo.VO.param.InboundParam;
import com.scu.stu.service.BookingService;
import com.scu.stu.service.InboundService;
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
public class InboundController {

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private Snowflake snowflake;

    @Resource
    private UserService userService;

    @Resource
    private BookingService bookingService;

    @Resource
    private RelationService relationService;

    @Resource
    private InboundService inboundService;

    @Resource(name = "rocketMQService")
    private RocketMQTemplate rocketMQService;

    @Value("${rocketmq.topic.pay}")
    private String pay;

    @Value("${rocketmq.topic.relation}")
    private String relation;

    private final String tokenCheck = "token";

    private final long expireTime = 30*60*1000; //30分钟

    @GetMapping("api/admin/getInboundList")
    public Result getList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
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

    @GetMapping("api/editor/getInboundList")
    public Result getEditorList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if (!RefreshToken(userId)) {
                return Result.logout();
            }
            InboundQuery query = JSON.parseObject(data, InboundQuery.class);

            //查询当前供应商下预约单
            BookingQuery bookingQuery = new BookingQuery();
            bookingQuery.setFarmerId(userId);
            List<BookingDTO> bookingDTOList = bookingService.queryNoPage(bookingQuery);

            if (bookingDTOList != null && !CollectionUtils.isEmpty(bookingDTOList)) {
                List<String> bookingIdList = bookingDTOList.stream().map(BookingDTO::getBookingId).collect(Collectors.toList());
                //查询关系表,获取入库单ID列表
                List<RelationDTO> relationDTOS = relationService.queryByBookingList(bookingIdList);
                if (relationDTOS != null && !CollectionUtils.isEmpty(relationDTOS)) {
                    List<String> inboundIdList = relationDTOS.stream().map(RelationDTO::getInboundId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(inboundIdList)) {
                        //查询入库单列表
                        List<InboundDTO> inboundDTOList = inboundService.batchQuery(inboundIdList, query);
                        if (inboundDTOList != null && !CollectionUtils.isEmpty(inboundDTOList)) {
                            List<InboundVO> inboundVOList = inboundDTOList.stream().map(inboundDTO -> {
                                InboundVO inboundVO = new InboundVO();
                                BeanUtils.copyProperties(inboundDTO, inboundVO);
                                inboundVO.setGMTCreate(DateUtils.format(inboundDTO.getGMTCreate()));
                                inboundVO.setInboundTime(DateUtils.format(inboundDTO.getInboundTime()));
                                return inboundVO;
                            }).collect(Collectors.toList());
                            return Result.success(inboundVOList);
                        }
                    }
                }
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/admin/getInboundTotal")
    public Result total(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            InboundQuery query = JSON.parseObject(data, InboundQuery.class);
            return Result.success(inboundService.total(query));
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/editor/getInboundTotal")
    public Result editorTotal(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if (!RefreshToken(userId)) {
                return Result.logout();
            }
            InboundQuery query = JSON.parseObject(data, InboundQuery.class);

            //查询当前供应商下预约单
            BookingQuery bookingQuery = new BookingQuery();
            bookingQuery.setFarmerId(userId);
            List<BookingDTO> bookingDTOList = bookingService.queryNoPage(bookingQuery);

            if (bookingDTOList != null && !CollectionUtils.isEmpty(bookingDTOList)) {
                List<String> bookingIdList = bookingDTOList.stream().map(BookingDTO::getBookingId).collect(Collectors.toList());
                //查询关系表,获取入库单ID列表
                List<RelationDTO> relationDTOS = relationService.queryByBookingList(bookingIdList);
                if (relationDTOS != null && !CollectionUtils.isEmpty(relationDTOS)) {
                    List<String> inboundIdList = relationDTOS.stream().map(RelationDTO::getInboundId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(inboundIdList)) {
                        //查询入库单列表
                        List<InboundDTO> inboundDTOList = inboundService.batchQuery(inboundIdList, query);
                        if(inboundDTOList != null && !CollectionUtils.isEmpty(inboundDTOList)) {
                            return Result.success(inboundDTOList.size());
                        }
                    }
                }
            }
            return Result.success(0);
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/getInboundDetail")
    public Result detail(@RequestParam(value = "token") String token, @RequestParam(value = "data") String inboundId){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            if(!RefreshToken(userId)){
                return Result.logout();
            }
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
            if(!RefreshToken(userId)){
                return Result.logout();
            }
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                return Result.error("供应商不能创建入库单，请联系仓库小二创建");
            }
            InboundParam param = JSON.parseObject(data, InboundParam.class);
            BookingQuery bookingQuery = new BookingQuery();
            bookingQuery.setBookingId(param.getBookingId());
            List<BookingDTO> bookingDTOS = bookingService.query(bookingQuery);
            if(bookingDTOS == null || CollectionUtils.isEmpty(bookingDTOS)){
                return Result.error("预约单无效，请输入正确的预约单ID");
            }

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

                //发送payOrder创建消息
                PayMessage message = new PayMessage();
                message.setBookingId(param.getBookingId());
                message.setInspectorId(userId);
                message.setType(PayType.INBOUND.getCode());// 1:入库
                List<Item> itemList = param.getItemList().stream().map(paramItem -> {
                    Item item = new Item();
                    BeanUtils.copyProperties(paramItem, item);
                    return item;
                }).collect(Collectors.toList());
                message.setItemList(itemList);
                Message<String> msg = MessageBuilder.withPayload(JSON.toJSONString(message)).build();
                SendResult sendResult = rocketMQService.syncSend(pay, msg);

                //发送relation更新信息
                RelationDTO relationDTO = new RelationDTO();
                relationDTO.setBookingId(param.getBookingId());
                relationDTO.setInboundId(inboundId);
                Message<String> msgs = MessageBuilder.withPayload(JSON.toJSONString(relationDTO)).build();
                SendResult sendResult2 = rocketMQService.syncSend(relation.concat(":INBOUND"), msgs);
                if(sendResult.getSendStatus() == SendStatus.SEND_OK && sendResult2.getSendStatus() == SendStatus.SEND_OK) {
                    if (inboundService.create(inboundDTO, inboundSubDTOList)) {
                        return Result.success();
                    }
                }
                return Result.error("入库单创建失败");
            } else {
                return Result.error("入库货品列表不能为空");
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
