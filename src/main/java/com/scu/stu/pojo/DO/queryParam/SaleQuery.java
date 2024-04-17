package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class SaleQuery {

    /**
     * page
     */
    private int page = 1;

    /**
     * limit
     */
    private int limit = 10;

    /**
     * ID
     */
    private String saleId;

    /**
     * 采购小二ID
     */
    private String purchaseId;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 状态
     */
    private int status;
}
