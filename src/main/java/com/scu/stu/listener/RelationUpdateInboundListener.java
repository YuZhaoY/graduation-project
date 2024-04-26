package com.scu.stu.listener;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.Result;
import com.scu.stu.mapper.RelationMapper;
import com.scu.stu.pojo.DO.RelationDO;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DTO.RelationDTO;
import lombok.SneakyThrows;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@Service
@RocketMQMessageListener(topic = "RELATION", consumerGroup = "CID_RELATION_INBOUND_GROUP", selectorExpression = "INBOUND", selectorType = SelectorType.TAG)
public class RelationUpdateInboundListener implements RocketMQListener<String> {

    @Resource
    private RelationMapper relationMapper;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(String message) {
        RelationDTO relationDTO = JSON.parseObject(message, RelationDTO.class);
        RelationQuery query = new RelationQuery();
        query.setBookingId(relationDTO.getBookingId());
        List<RelationDO> relationDOS = relationMapper.query(query);
        if(relationDOS != null && !CollectionUtils.isEmpty(relationDOS)){
            RelationDO relationDO = relationDOS.get(0);
            if(relationDO.getInboundId() != null && !"".equals(relationDO.getInboundId())) {
                throw new RuntimeException("该订单已入库，无法再次入库");
            }
            relationDO.setInboundId(relationDTO.getInboundId());
            relationMapper.update(relationDO);
        } else {
            throw new SQLException("未查到预约单");
        }
    }
}
