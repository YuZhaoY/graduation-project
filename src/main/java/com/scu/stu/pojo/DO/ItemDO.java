package com.scu.stu.pojo.DO;

import lombok.Data;

@Data
public class ItemDO {

    /**
     * ID
     */
    private String itemId;

    /**
     * 名称
     */
    private String name;

    /**
     * 规格
     */
    private String specification;

    /**
     * 单位
     */
    private String unit;

    /**
     * 图片
     */
    private String picUrl;

    /**
     * 类别
     */
    private String category;
}
