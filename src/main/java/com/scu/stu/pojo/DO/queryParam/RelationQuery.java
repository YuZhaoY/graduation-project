package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class RelationQuery {

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
    private String id;

    /**
     * 补货计划ID
     */
    private String replenishmentId;

    /**
     * 采购单ID
     */
    private String saleId;

    /**
     * 预约单ID
     */
    private String bookingId;

    /**
     * 入库单ID
     */
    private String inboundId;

    /**
     * 付款单ID
     */
    private String payId;

    /**
     * 退供单ID
     */
    private String refundId;

    /**
     * 退供收款单ID
     */
    private String returnId;
}
