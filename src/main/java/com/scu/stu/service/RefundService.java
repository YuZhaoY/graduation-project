package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.RefundQuery;
import com.scu.stu.pojo.DTO.RefundDTO;
import com.scu.stu.pojo.DTO.RefundSubDTO;

import java.util.List;

public interface RefundService {

    /**
     * 查询
     */
    List<RefundDTO> query(RefundQuery query);

    /**
     * 查询退供单总数
     */
    int total(RefundQuery query);

    /**
     * 查询退供单详情
     */
    List<RefundSubDTO> detail(String refundId);

    /**
     * 创建退供单
     */
    boolean create(RefundDTO refundDTO, List<RefundSubDTO> refundSubDTOList);
}
