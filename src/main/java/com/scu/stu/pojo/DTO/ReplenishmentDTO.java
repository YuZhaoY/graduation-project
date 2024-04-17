package com.scu.stu.pojo.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class ReplenishmentDTO {

    /**
     * ID
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

    /**
     * 版本
     */
    private int version;
}
