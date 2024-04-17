package com.scu.stu.pojo.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReplenishmentSubVO {

    /**
     * 货品ID
     */
    private String itemId;

    /**
     * 货品数量
     */
    private BigDecimal quantity;
}
