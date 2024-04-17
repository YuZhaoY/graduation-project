package com.scu.stu.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "RELATION", consumerGroup = "CID_RELATION_GROUP", selectorExpression = "INBOUND", selectorType = SelectorType.TAG)
public class RelationUpdateInboundListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("消费者接收消息：" + message);
    }
}
