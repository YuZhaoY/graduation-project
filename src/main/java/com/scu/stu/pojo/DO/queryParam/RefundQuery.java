package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class RefundQuery {

    /**
     * page
     */
    private int page = 1;

    /**
     * limit
     */
    private int limit = 10;

    /**
     * 入库单ID
     */
    private String refundId;

    /**
     * 仓Id
     */
    private String storeId;

    /**
     * 质检小二
     */
    private String inspectorId;
}
