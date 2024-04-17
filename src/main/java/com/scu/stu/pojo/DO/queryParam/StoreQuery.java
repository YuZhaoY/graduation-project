package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class StoreQuery {

    /**
     * page
     */
    private int page = 1;

    /**
     * limit
     */
    private int limit = 10;

    /**
     * 仓code
     */
    private String storeId;

    /**
     * 仓name
     */
    private String name;

    /**
     * 省份
     */
    private String province;
}
