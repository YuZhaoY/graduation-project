package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.BookingQuery;
import com.scu.stu.pojo.DTO.BookingDTO;

import java.util.List;

public interface BookingService {

    /**
     * 查询
     */
    List<BookingDTO> query(BookingQuery query);

    /**
     * 不分页查询
     */
    List<BookingDTO> queryNoPage(BookingQuery query);

    /**
     * 批量查
     */
    List<BookingDTO> batchQuery(List<String> bookingIdList);

    /**
     * 预约单总数
     */
    int total(BookingQuery query);

    /**
     * 创建预约单
     */
    boolean create(BookingDTO bookingDTO, String saleId);

    /**
     * 更新预约单
     */
    boolean update(BookingDTO bookingDTO);
}
