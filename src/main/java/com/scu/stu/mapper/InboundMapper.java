package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.InboundDO;
import com.scu.stu.pojo.DO.InboundSubDO;
import com.scu.stu.pojo.DO.queryParam.InboundQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface InboundMapper {

    List<InboundDO> query(InboundQuery query);

    List<InboundDO> batchQuery(@Param("idList") List<String> idList, @Param("query") InboundQuery query);

    int total(InboundQuery query);

    List<InboundSubDO> querySub(String inboundId);

    boolean create(InboundDO inboundDO);

    boolean createSub(List<InboundSubDO> inboundSubDOList);
}
