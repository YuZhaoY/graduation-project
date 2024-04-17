package com.scu.stu.pojo.VO;

import lombok.Data;

@Data
public class ReplenishmentVO {

    /**
     * ID
     */
    private String replenishmentId;

    /**
     * 创建时间
     */
    private String GMTCreate;

    /**
     * 采购小二ID
     */
    private String purchaseId;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 开始日期
     */
    private String startExpiration;

    /**
     * 截止日期
     */
    private String expiration;

    /**
     * 计划状态
     */
    private String status;
}
