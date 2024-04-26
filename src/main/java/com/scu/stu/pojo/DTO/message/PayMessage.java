package com.scu.stu.pojo.DTO.message;

import lombok.Data;

import java.util.List;

@Data
public class PayMessage {

    /**
     * 预约单ID
     */
    private String bookingId;

    /**
     * 付款单类型（1:入库, 2:退供）
     */
    private int type;

    /**
     * 质检小二
     */
    private String inspectorId;


    /**
     * 货品列表
     */
    private List<Item> itemList;
}
