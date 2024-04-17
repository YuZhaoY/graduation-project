package com.scu.stu.listener;

import com.alibaba.fastjson.JSON;
import com.scu.stu.mapper.RelationMapper;
import com.scu.stu.pojo.DO.RelationDO;
import com.scu.stu.pojo.DTO.RelationDTO;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@RocketMQMessageListener(topic = "RELATION", consumerGroup = "CID_RELATION_GROUP", selectorExpression = "CREATE", selectorType = SelectorType.TAG)
public class RelationCreateListener implements RocketMQListener<String> {

    @Resource
    private RelationMapper relationMapper;

    @Override
    public void onMessage(String message) {
        RelationDTO relationDTO = JSON.parseObject(message, RelationDTO.class);
        RelationDO relationDO = new RelationDO();
        relationDO.setReplenishmentId(relationDTO.getReplenishmentId());
        relationDO.setSaleId(relationDTO.getSaleId());
        relationMapper.create(relationDO);
    }
}
