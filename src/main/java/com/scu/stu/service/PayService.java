package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.PayQuery;
import com.scu.stu.pojo.DTO.PayDTO;

import java.util.List;

public interface PayService {

    /**
     * 查询
     */
    List<PayDTO> query(PayQuery query);

    /**
     * 查询总数
     */
    int total(PayQuery query);

    /**
     * 创建
     */
    boolean create(PayDTO payDTO);

    /**
     * 更新
     */
    boolean update(PayDTO payDTO);
}
