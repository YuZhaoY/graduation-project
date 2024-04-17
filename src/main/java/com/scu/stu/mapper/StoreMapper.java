package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.StoreDO;
import com.scu.stu.pojo.DO.queryParam.StoreQuery;

import java.util.*;

public interface StoreMapper {

    int total(StoreQuery query);

    List<StoreDO> query(StoreQuery query);

    String queryStoreId();

    boolean update(StoreDO storeDO);

    boolean delete(String storeId);

    boolean create(StoreDO storeDO);
}
