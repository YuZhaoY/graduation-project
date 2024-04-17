package com.scu.stu.pojo.DO;

import lombok.Data;

import java.util.Date;

@Data
public class RefundDO {

    /**
     * 退供单ID
     */
    private String refundId;

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
     * 退供时间
     */
    private Date refundTime;
}
