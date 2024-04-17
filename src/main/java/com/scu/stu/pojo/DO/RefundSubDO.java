package com.scu.stu.pojo.DO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundSubDO {

    /**
     * 退供单ID
     */
    private String refundId;

    /**
     * 货品Id
     */
    private String itemId;

    /**
     * 货品数量
     */
    private BigDecimal quantity;
}
