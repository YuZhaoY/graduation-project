package com.scu.stu.pojo.VO.param;

import lombok.Data;

@Data
public class StoreParam {

    /**
     * 仓code
     */
    private String storeId;

    /**
     * 仓name
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 省份
     */
    private String province;
}
