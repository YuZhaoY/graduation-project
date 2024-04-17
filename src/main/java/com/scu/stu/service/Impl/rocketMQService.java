package com.scu.stu.service.Impl;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

@ExtRocketMQTemplateConfiguration(nameServer = "${rocketmq.name-server}")
public class rocketMQService extends RocketMQTemplate {
}
