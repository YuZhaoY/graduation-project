package com.scu.stu.listener;

import com.alibaba.fastjson.JSON;
import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import com.scu.stu.pojo.DTO.SaleDTO;
import com.scu.stu.pojo.DTO.message.SaleMessage;
import com.scu.stu.service.SaleService;
import lombok.SneakyThrows;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Service
@RocketMQMessageListener(topic = "SALE_UPDATE", consumerGroup = "CID_SALE_REFUND_GROUP", selectorExpression = "REFUND", selectorType = SelectorType.TAG)
public class SaleOrderReturnListener implements RocketMQListener<String> {

    @Resource
    private SaleService saleService;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(String message) {
        SaleMessage saleMessage = JSON.parseObject(message, SaleMessage.class);
        SaleQuery query = new SaleQuery();
        query.setSaleId(saleMessage.getSaleId());
        List<SaleDTO> saleDTOS = saleService.getList(query);
        if(saleDTOS != null && !CollectionUtils.isEmpty(saleDTOS)) {
            SaleDTO saleDTO = saleDTOS.get(0);
            if(Objects.requireNonNull(saleDTO.getReturnAmount()).compareTo(BigDecimal.valueOf(0.00)) != 0) {
                throw new RuntimeException("采购单已生成退供单，无法更新");
            }
            saleDTO.setReturnAmount(saleMessage.getAmount());
            saleService.updateSale(saleDTO);
        } else {
            throw new SQLException("未查到采购单");
        }
    }
}
