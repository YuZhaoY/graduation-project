package com.scu.stu.pojo.DTO;

import lombok.Data;

@Data
public class PayMessage {

    /**
     * 预约单ID
     */
    private String bookingId;

    /**
     * 付款单类型（1:入库, 2:退供）
     */
    private int type;

    /**
     * 质检小二
     */
    private String inspectorId;
}
