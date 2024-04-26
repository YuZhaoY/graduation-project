package com.scu.stu.pojo.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayVO {

    /**
     * 付款单ID
     */
    private String payId;

    /**
     * 创建时间
     */
    private String GMTCreate;

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
     * 付款单类型（入库、退供）
     */
    private String type;

    /**
     * 协议状态
     */
    private String status;

    /**
     * 供应商收款二维码
     */
    private String QRCode;
}
