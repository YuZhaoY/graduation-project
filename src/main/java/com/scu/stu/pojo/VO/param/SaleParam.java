package com.scu.stu.pojo.VO.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleParam {

    /**
     * 采购单ID
     */
    private String saleId;

    /**
     * 补货计划ID
     */
    private String replenishmentId;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 采购单详情
     */
    private List<SaleSubParam> itemList;
}
