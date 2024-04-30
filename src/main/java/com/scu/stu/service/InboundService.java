package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.InboundQuery;
import com.scu.stu.pojo.DTO.InboundDTO;
import com.scu.stu.pojo.DTO.InboundSubDTO;

import java.util.List;

public interface InboundService {

    /**
     * 查询
     */
    List<InboundDTO> query(InboundQuery query);

    /**
     * 批量查询
     */
    List<InboundDTO> batchQuery(List<String> inboundIdList, InboundQuery query);

    /**
     * 查询入库单总数
     */
    int total(InboundQuery query);

    /**
     * 查询入库单详情
     */
    List<InboundSubDTO> detail(String inboundId);

    /**
     * 创建入库单
     */
    boolean create(InboundDTO inboundDTO, List<InboundSubDTO> inboundSubDTOList);
}
