package com.scu.stu.pojo.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InboundSubDTO {

    /**
     * 入库单ID
     */
    private String inboundId;

    /**
     * 货品Id
     */
    private String itemId;

    /**
     * 货品数量
     */
    private BigDecimal quantity;
}
