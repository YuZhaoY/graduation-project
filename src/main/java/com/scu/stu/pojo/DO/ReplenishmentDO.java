package com.scu.stu.pojo.DO;

import lombok.Data;

import java.util.*;

@Data
public class ReplenishmentDO {

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
    private Date startExpiration;

    /**
     * 截止日期
     */
    private Date expiration;

    /**
     * 计划状态
     */
    private int status;

    /**
     * 版本
     */
    private int version;
}
