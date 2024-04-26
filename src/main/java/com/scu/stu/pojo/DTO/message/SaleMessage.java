package com.scu.stu.pojo.DTO.message;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleMessage {

    /**
     * ID
     */
    private String saleId;

    /**
     * 金额
     */
    private BigDecimal amount;
}
