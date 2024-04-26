package com.scu.stu.pojo.DTO.message;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Item {

    /**
     * 货品Id
     */
    private String itemId;

    /**
     * 货品数量
     */
    private BigDecimal quantity;
}
