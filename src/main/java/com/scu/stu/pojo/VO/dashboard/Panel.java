package com.scu.stu.pojo.VO.dashboard;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Panel {

    /**
     * 生效中的补货计划总数
     */
    private int replenishment;

    /**
     * 生效中的采购单总数
     */
    private int sale;

    /**
     * 采购单已付款总金额
     */
    private BigDecimal payAmount;

    /**
     * 采购单未付款金额
     */
    private BigDecimal freezeAmount;
}
