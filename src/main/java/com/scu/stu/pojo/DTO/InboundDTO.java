package com.scu.stu.pojo.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class InboundDTO {

    /**
     * 入库单ID
     */
    private String inboundId;

    /**
     * 创建时间
     */
    private Date GMTCreate;

    /**
     * 修改时间
     */
    private Date GMTModified;

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
    private Date inboundTime;
}
