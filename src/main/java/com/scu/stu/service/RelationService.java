package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DTO.RelationDTO;

import java.util.List;

public interface RelationService {

    /**
     * 批量查询
     */
    List<RelationDTO> queryByBookingList(List<String> bookingList);

    /**
     * 查询
     */
    List<RelationDTO> query(RelationQuery query);

    /**
     * 创建
     */
    boolean create(RelationDTO relationDTO);

    /**
     * 更新
     */
    boolean update(RelationDTO relationDTO);
}
