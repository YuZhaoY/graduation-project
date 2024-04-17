package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.BookingDO;
import com.scu.stu.pojo.DO.queryParam.BookingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BookingMapper {

    List<BookingDO> query(BookingQuery query);

    List<BookingDO> batchQuery(List<String> bookingIdList);

    int total(BookingQuery query);

    boolean create(BookingDO bookingDO);

    boolean update(BookingDO bookingDO);
}
