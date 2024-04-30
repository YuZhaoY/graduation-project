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
     * 根据采购单ID批量查询
     * @param saleIdList
     * @return
     */
    List<RelationDTO> queryBySaleIdList(List<String> saleIdList);

    /**
     * 批量查询
     */
    List<RelationDTO> queryByIdListPage(List<Integer> idList, RelationQuery query);

    /**
     * 查询总数
     */
    int total(RelationQuery query);

    /**
     * 创建
     */
    boolean create(RelationDTO relationDTO);

    /**
     * 更新
     */
    boolean update(RelationDTO relationDTO);
}
