package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class InboundQuery {

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
    private String inboundId;

    /**
     * 仓Id
     */
    private String storeId;

    /**
     * 质检入库小二
     */
    private String inspectorId;
}
