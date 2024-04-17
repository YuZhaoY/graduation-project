package com.scu.stu.pojo.VO.param;

import lombok.Data;

import java.util.List;

@Data
public class InboundParam {

    /**
     * 预约单ID
     */
    private String bookingId;

    /**
     * 仓Id
     */
    private String storeId;

    /**
     * 入库时间
     */
    private String inboundTime;

    /**
     * 货品列表
     */
    private List<InboundSubParam> itemList;
}
