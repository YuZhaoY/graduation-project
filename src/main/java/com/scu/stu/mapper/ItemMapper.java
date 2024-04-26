package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.ItemDO;
import com.scu.stu.pojo.DO.queryParam.ItemQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface ItemMapper {

    String queryItemId();

    List<ItemDO> query(ItemQuery itemQuery);

    int total(ItemQuery itemQuery);

    boolean create(ItemDO itemDO);

    boolean update(ItemDO itemDO);

    boolean delete(String itemId);
}
