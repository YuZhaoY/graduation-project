package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.SaleDO;
import com.scu.stu.pojo.DO.SaleSubDO;
import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface SaleMapper {

    List<SaleDO> querySaleOrder(SaleQuery query);

    int total(SaleQuery query);

    List<SaleSubDO> querySaleSubOrder(String saleId);

    boolean insertSaleOrder(SaleDO saleDO);

    boolean updateSaleOrder(SaleDO saleDO);

    boolean batchInsertSub(List<SaleSubDO> saleSubDOS);

    boolean batchDeleteSub(List<SaleSubDO> saleSubDOS);

    boolean batchUpdateSub(List<SaleSubDO> saleSubDOS);
}
