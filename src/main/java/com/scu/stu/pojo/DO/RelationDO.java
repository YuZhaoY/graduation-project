package com.scu.stu.pojo.DO;

import lombok.Data;

@Data
public class RelationDO {

    /**
     * 主键ID
     */
    private int id;
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
