package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class PayQuery {

    /**
     * 付款单ID
     */
    private String payId;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 质检小二ID
     */
    private String inspectorId;

    /**
     * 付款单类型（入库、退供）
     */
    private int type;

    /**
     * 协议状态
     */
    private int status;
}
