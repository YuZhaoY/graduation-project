package com.scu.stu.pojo.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SaleDTO {

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
     * 采购小二ID
     */
    private String purchaseId;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 退供金额
     */
    private BigDecimal returnAmount;

    /**
     * 已付款金额
     */
    private BigDecimal payAmount;

    /**
     * 冻结金额（已生成付款单还未付款）
     */
    private BigDecimal freezeAmount;

    /**
     * 状态
     */
    private int status;

    /**
     * 版本
     */
    private int version;
}
