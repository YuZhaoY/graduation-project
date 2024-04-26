package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.ReplenishmentDO;
import com.scu.stu.pojo.DO.ReplenishmentSubDO;
import com.scu.stu.pojo.DO.queryParam.ReplenishmentQuery;
import com.scu.stu.pojo.DO.queryParam.ReplenishmentSubQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface ReplenishmentMapper {

    List<ReplenishmentDO> query(ReplenishmentQuery query);

    int total(ReplenishmentQuery query);

    boolean create(ReplenishmentDO replenishmentDO);

    boolean update(ReplenishmentDO replenishmentDO);

    boolean updateInvalid();

    boolean updateValid();

    List<ReplenishmentSubDO> querySub(ReplenishmentSubQuery query);

    List<ReplenishmentSubDO> batchQuerySub(List<String> replenishmentIdList);

    boolean batchCreateSub(List<ReplenishmentSubDO> replenishmentSubDOS);

    boolean batchDeleteSub(List<ReplenishmentSubDO> replenishmentSubDOS);

    boolean batchUpdateSub(List<ReplenishmentSubDO> replenishmentSubDOS);
}
