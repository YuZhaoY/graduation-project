package com.scu.stu.pojo.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ItemDTO {

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
    private List<String> specification;

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
