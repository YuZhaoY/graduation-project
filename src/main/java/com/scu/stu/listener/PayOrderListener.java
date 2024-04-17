package com.scu.stu.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "PAY_ORDER", consumerGroup = "CID_PAY_ORDER_GROUP")
public class PayOrderListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("消费者接收消息：" + message);
    }
}