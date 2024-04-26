package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.StoreDO;
import com.scu.stu.pojo.DO.queryParam.StoreQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Mapper
public interface StoreMapper {

    int total(StoreQuery query);

    List<StoreDO> query(StoreQuery query);

    String queryStoreId();

    boolean update(StoreDO storeDO);

    boolean delete(String storeId);

    boolean create(StoreDO storeDO);
}
