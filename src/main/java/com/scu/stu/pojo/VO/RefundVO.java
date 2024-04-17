package com.scu.stu.pojo.VO;

import lombok.Data;

import java.util.Date;

@Data
public class RefundVO {

    /**
     * 退供单ID
     */
    private String refundId;

    /**
     * 创建时间
     */
    private String GMTCreate;

    /**
     * 质检小二
     */
    private String inspectorId;

    /**
     * 仓Id
     */
    private String storeId;

    /**
     * 退供时间
     */
    private String refundTime;
}
