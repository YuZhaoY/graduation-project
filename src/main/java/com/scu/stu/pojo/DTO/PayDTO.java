package com.scu.stu.pojo.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayDTO {

    /**
     * 付款单ID
     */
    private String payId;

    /**
     * 创建时间
     */
    private Date GMTCreate;

    /**
     * 修改时间
     */
    private Date GMTModified;

    /**
     * 付款金额
     */
    private BigDecimal payAmount;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 质检小二ID
     */
    private String inspectorId;

    /**
     * 付款单类型（1:入库, 2:退供）
     */
    private int type;

    /**
     * 协议状态
     */
    private int status;

    /**
     * 版本号
     */
    private int version;
}
