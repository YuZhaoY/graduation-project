package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class ReplenishmentSubQuery {

    /**
     * 供货协议主表ID
     */
    private String replenishmentId;

    /**
     * 货品ID
     */
    private String itemId;
}
