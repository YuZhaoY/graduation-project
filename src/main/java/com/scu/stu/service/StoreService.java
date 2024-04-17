package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.StoreQuery;
import com.scu.stu.pojo.DTO.StoreDTO;

import java.util.*;

public interface StoreService {

    /**
     * 查询
     * @param query
     * @return
     */
    List<StoreDTO> query(StoreQuery query);

    /**
     * 查询所有
     * @param query
     * @return
     */
    List<StoreDTO> queryAll(StoreQuery query);

    /**
     * 查询记录总数
     * @param query
     * @return
     */
    int total(StoreQuery query);

    /**
     * 更新
     * @param storeDTO
     * @return
     */
    boolean update(StoreDTO storeDTO);

    /**
     * 删除
     * @param storeId
     * @return
     */
    boolean delete(String storeId);

    /**
     * 新增
     * @param storeDTO
     * @return
     */
    boolean create(StoreDTO storeDTO);

    /**
     * 获取最新一个仓code
     * @return
     */
    String getStoreId();
}
