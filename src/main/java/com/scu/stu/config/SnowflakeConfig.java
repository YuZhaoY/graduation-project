package com.scu.stu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import wiki.xsx.core.snowflake.config.Snowflake;

@Configuration
public class SnowflakeConfig {

    @Value("${snowflake.data-center-id}")
    private int dataCenterId;

    @Value("${snowflake.worker-id}")
    private int workerId;

    @Bean
    public Snowflake snowflake() {
        // 参数1表示数据中心ID，参数2表示机器ID
        return new Snowflake(workerId, dataCenterId);
    }
}
