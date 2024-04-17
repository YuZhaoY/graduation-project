package com.scu.stu.pojo.DO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReplenishmentSubDO {

    /**
     * 供货协议主表ID
     */
    private String replenishmentId;

    /**
     * 创建时间
     */
    private Date GMTCreate;

    /**
     * 修改时间
     */
    private Date GMTModified;

    /**
     * 货品ID
     */
    private String itemId;

    /**
     * 货品数量
     */
    private BigDecimal quantity;
}
