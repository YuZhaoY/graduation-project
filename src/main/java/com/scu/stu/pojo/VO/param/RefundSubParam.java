package com.scu.stu.pojo.VO.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundSubParam {

    /**
     * 货品Id
     */
    private String itemId;

    /**
     * 货品数量
     */
    private BigDecimal quantity;
}
