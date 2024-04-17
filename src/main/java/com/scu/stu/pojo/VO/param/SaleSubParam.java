package com.scu.stu.pojo.VO.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleSubParam {

    /**
     * 货品ID
     */
    private String itemId;

    /**
     * 采购价
     */
    private BigDecimal price;

    /**
     * 采购量
     */
    private BigDecimal quantity;
}
