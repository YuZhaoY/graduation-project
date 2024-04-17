package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.ItemQuery;
import com.scu.stu.pojo.DTO.ItemDTO;

import java.util.List;

public interface ItemService {

    /**
     * 查询货品信息
     * @param query
     */
    List<ItemDTO> query(ItemQuery query);

    /**
     * 查询货品列表长度
     * @param query
     */
    int total(ItemQuery query);

    /**
     * 创建货品
     * @param itemDTO
     */
    boolean create(ItemDTO itemDTO);

    /**
     * 获取最新一条Item_id
     */
    String getItemId();

    /**
     * 更新数据
     * @param itemDTO
     */
    boolean update(ItemDTO itemDTO);

    /**
     * 删除货品
     * @param itemId
     * @return
     */
    boolean delete(String itemId);
}
