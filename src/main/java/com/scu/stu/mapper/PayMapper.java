package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.PayDO;
import com.scu.stu.pojo.DO.queryParam.PayQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface PayMapper {

    boolean create(PayDO payDO);

    boolean update(PayDO payDO);

    List<PayDO> query(PayQuery query);

    int total(PayQuery query);
}
