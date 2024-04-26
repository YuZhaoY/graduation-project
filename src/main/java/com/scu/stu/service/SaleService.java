package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import com.scu.stu.pojo.DTO.ReplenishmentSubDTO;
import com.scu.stu.pojo.DTO.SaleDTO;
import com.scu.stu.pojo.DTO.SaleSubDTO;

import java.util.List;

public interface SaleService {

    /**
     * 查询采购单列表
     */
    List<SaleDTO> getList(SaleQuery query);

    /**
     * 采购单列表总数
     */
    int total(SaleQuery query);

    /**
     * 采购单详情
     */
    List<SaleSubDTO> getDetail(String saleId);

    /**
     * 创建采购单
     */
    boolean create(SaleDTO saleDTO, List<SaleSubDTO> saleSubDTOS);

    /**
     * 更新采购单状态
     */
    boolean updateSale(SaleDTO saleDTO);

    /**
     * 更新采购单
     */
    boolean update(
            SaleDTO saleDTO,
            List<SaleSubDTO> updateSubDTOS,
            List<SaleSubDTO> createSubDTOS,
            List<SaleSubDTO> deleteSubDTOS
    );
}
