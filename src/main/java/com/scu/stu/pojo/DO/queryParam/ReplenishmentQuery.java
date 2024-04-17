package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class ReplenishmentQuery {

    /**
     * page
     */
    private int page = 1;

    /**
     * limit
     */
    private int limit = 10;

    /**
     * ID
     */
    private String replenishmentId;

    /**
     * 采购小二ID
     */
    private String purchaseId;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 计划状态
     */
    private int status;
}
