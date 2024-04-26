package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.RefundDO;
import com.scu.stu.pojo.DO.RefundSubDO;
import com.scu.stu.pojo.DO.queryParam.RefundQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface RefundMapper {

    List<RefundDO> query(RefundQuery query);

    int total(RefundQuery query);

    List<RefundSubDO> querySub(String refundId);

    boolean create(RefundDO refundDO);

    boolean createSub(List<RefundSubDO> refundSubDOList);
}
