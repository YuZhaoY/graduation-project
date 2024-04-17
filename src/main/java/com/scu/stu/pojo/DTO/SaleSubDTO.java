package com.scu.stu.pojo.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SaleSubDTO {

    /**
     * ID
     */
    private String saleId;

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
     * 采购价
     */
    private BigDecimal price;

    /**
     * 采购量
     */
    private BigDecimal quantity;
}
