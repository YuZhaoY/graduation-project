package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.RelationDO;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RelationMapper {

    List<RelationDO> query(RelationQuery query);

    boolean create(RelationDO relationDO);

    boolean update(RelationDO relationDO);

    List<RelationDO> queryByBookingList(List<String> bookingList);
}