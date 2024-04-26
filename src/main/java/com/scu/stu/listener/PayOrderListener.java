package com.scu.stu.listener;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.pay.PayStatus;
import com.scu.stu.common.pay.PayType;
import com.scu.stu.pojo.DO.queryParam.BookingQuery;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DTO.*;
import com.scu.stu.pojo.DTO.message.Item;
import com.scu.stu.pojo.DTO.message.PayMessage;
import com.scu.stu.pojo.DTO.message.SaleMessage;
import com.scu.stu.service.*;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import wiki.xsx.core.snowflake.config.Snowflake;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Service
@RocketMQMessageListener(topic = "PAY_ORDER", consumerGroup = "CID_PAY_ORDER_GROUP")
public class PayOrderListener implements RocketMQListener<String> {

    @Resource
    private BookingService bookingService;

    @Resource
    private PayService payService;

    @Autowired
    private Snowflake snowflake;

    @Resource
    private RelationService relationService;

    @Resource
    private SaleService saleService;

    @Resource(name = "rocketMQService")
    private RocketMQTemplate rocketMQService;

    @Value("${rocketmq.topic.sale}")
    private String sale;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(String message) {
        PayMessage payMessage = JSON.parseObject(message, PayMessage.class);
        BookingQuery query = new BookingQuery();
        query.setBookingId(payMessage.getBookingId());
        List<BookingDTO> bookingDTOList = bookingService.query(query);
        if (bookingDTOList != null && !CollectionUtils.isEmpty(bookingDTOList)) {
            BookingDTO bookingDTO = bookingDTOList.get(0);
            PayDTO payDTO = new PayDTO();
            String payId = snowflake.nextIdStr();
            BeanUtils.copyProperties(payMessage, payDTO);
            payDTO.setFarmerId(bookingDTO.getFarmerId());
            payDTO.setPayAmount(getPayAmount(payMessage.getBookingId(), payMessage.getItemList(), payId, payMessage.getType()));
            payDTO.setStatus(PayStatus.CREATE.getCode());
            payDTO.setPayId(payId);
            payService.create(payDTO);
        } else {
            throw new SQLException("未查到预约单");
        }
    }

    public BigDecimal getPayAmount(String bookingId, List<Item> itemList, String payId, int type) {
        RelationQuery query = new RelationQuery();
        query.setBookingId(bookingId);
        List<RelationDTO> relationDTOList = relationService.query(query);
        if (relationDTOList != null && !CollectionUtils.isEmpty(relationDTOList)) {
            RelationDTO relationDTO = relationDTOList.get(0);

            if(type == PayType.INBOUND.getCode()) {
                if (relationDTO.getPayId() != null && !"".equals(relationDTO.getPayId())) {
                    throw new RuntimeException("该订单已付款，无法再次付款");
                }
                relationDTO.setPayId(payId);
            } else {
                if (relationDTO.getReturnId() != null && !"".equals(relationDTO.getReturnId())) {
                    throw new RuntimeException("该订单已付款，无法再次付款");
                }
                relationDTO.setReturnId(payId);
            }
            relationService.update(relationDTO);
            List<SaleSubDTO> saleDetail = saleService.getDetail(relationDTO.getSaleId());
            BigDecimal totalAmount = BigDecimal.valueOf(0);
            for (Item item : itemList) {
                for (SaleSubDTO saleSubDTO : saleDetail) {
                    if (item.getItemId().equals(saleSubDTO.getItemId())) {
                        totalAmount = totalAmount.add(item.getQuantity().multiply(saleSubDTO.getPrice()));
                    }
                }
            }
            //发送更新采购单冻账金额消息
            SaleMessage message = new SaleMessage();
            message.setSaleId(relationDTO.getSaleId());
            message.setAmount(totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
            Message<String> msgs = MessageBuilder.withPayload(JSON.toJSONString(message)).build();
            String topic = type == PayType.INBOUND.getCode() ? sale.concat(":FREEZE") : sale.concat(":REFUND");
            SendResult sendResult = rocketMQService.syncSend(topic, msgs);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                return totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                throw new RuntimeException("创建付款单失败");
            }
        } else {
            throw new RuntimeException("未查询到预约单");
        }
    }
}
