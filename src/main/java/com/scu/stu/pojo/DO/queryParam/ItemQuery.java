package com.scu.stu.pojo.DO.queryParam;

import lombok.Data;

@Data
public class ItemQuery {

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
    private String itemId;

    /**
     * 名称
     */
    private String name;

    /**
     * 类目
     */
    private String category;

}
