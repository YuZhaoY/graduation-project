package com.scu.stu.pojo.VO.param;

import lombok.Data;

import java.util.List;

@Data
public class ReplenishmentParam {

    /**
     * ID
     */
    private String replenishmentId;

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
     * 货品列表
     */
    private List<ReplenishmentSubParam> itemList;
}
