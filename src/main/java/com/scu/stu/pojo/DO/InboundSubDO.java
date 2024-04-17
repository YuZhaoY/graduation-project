package com.scu.stu.pojo.DO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InboundSubDO {

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
