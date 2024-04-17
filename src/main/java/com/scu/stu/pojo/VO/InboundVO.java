package com.scu.stu.pojo.VO;

import lombok.Data;

import java.util.Date;

@Data
public class InboundVO {

    /**
     * 入库单ID
     */
    private String inboundId;

    /**
     * 创建时间
     */
    private String GMTCreate;

    /**
     * 质检入库小二
     */
    private String inspectorId;

    /**
     * 仓Id
     */
    private String storeId;

    /**
     * 入库时间
     */
    private String inboundTime;
}
