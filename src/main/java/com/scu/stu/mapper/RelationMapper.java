package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.RelationDO;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface RelationMapper {

    List<RelationDO> query(RelationQuery query);

    int total(RelationQuery query);

    boolean create(RelationDO relationDO);

    boolean update(RelationDO relationDO);

    List<RelationDO> queryByBookingList(List<String> bookingList);

    List<RelationDO> queryBySaleIdList(List<String> saleIdList);

    List<RelationDO> batchQuery(@Param("idList") List<Integer> idList, @Param("query") RelationQuery query);
}
