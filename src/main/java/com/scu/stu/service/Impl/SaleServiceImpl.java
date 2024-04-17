package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.mapper.SaleMapper;
import com.scu.stu.pojo.DO.SaleDO;
import com.scu.stu.pojo.DO.SaleSubDO;
import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import com.scu.stu.pojo.DTO.SaleDTO;
import com.scu.stu.pojo.DTO.SaleSubDTO;
import com.scu.stu.service.SaleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleServiceImpl implements SaleService {

    @Resource
    private SaleMapper saleMapper;

    @Override
    public List<SaleDTO> getList(SaleQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<SaleDO> saleDOS = saleMapper.querySaleOrder(query);
        if(saleDOS!=null && !CollectionUtils.isEmpty(saleDOS)) {
            List<SaleDTO> saleDTOS = saleDOS.stream().map(saleDO -> {
                SaleDTO saleDTO = new SaleDTO();
                BeanUtils.copyProperties(saleDO,saleDTO);
                return saleDTO;
            }).collect(Collectors.toList());
            PageInfo<SaleDTO> pageInfo = new PageInfo<>(saleDTOS);
            return pageInfo.getList();
        }
        return null;
    }

    @Override
    public int total(SaleQuery query) {
        return saleMapper.total(query);
    }

    @Override
    public List<SaleSubDTO> getDetail(String saleId) {
        List<SaleSubDO> saleSubDOS = saleMapper.querySaleSubOrder(saleId);
        if(saleSubDOS!=null && !CollectionUtils.isEmpty(saleSubDOS)) {
            List<SaleSubDTO> saleSubDTOS = saleSubDOS.stream().map(saleSubDO -> {
                SaleSubDTO saleSubDTO = new SaleSubDTO();
                BeanUtils.copyProperties(saleSubDO,saleSubDTO);
                return saleSubDTO;
            }).collect(Collectors.toList());
            return saleSubDTOS;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(SaleDTO saleDTO, List<SaleSubDTO> saleSubDTOS) {
        SaleDO saleDO = new SaleDO();
        BeanUtils.copyProperties(saleDTO, saleDO);
        List<SaleSubDO> saleSubDOS = saleSubDTOS.stream().map(saleSubDTO -> {
            SaleSubDO saleSubDO = new SaleSubDO();
            BeanUtils.copyProperties(saleSubDTO, saleSubDO);
            return saleSubDO;
        }).collect(Collectors.toList());
        return saleMapper.insertSaleOrder(saleDO) && saleMapper.batchInsertSub(saleSubDOS);
    }

    @Override
    public boolean updateStatus(SaleDTO saleDTO) {
        SaleDO saleDO = new SaleDO();
        BeanUtils.copyProperties(saleDTO, saleDO);
        return saleMapper.updateSaleOrder(saleDO);
    }

    @Override
    public boolean update(SaleDTO saleDTO, List<SaleSubDTO> updateSubDTOS, List<SaleSubDTO> createSubDTOS, List<SaleSubDTO> deleteSubDTOS) {
        SaleDO saleDO = new SaleDO();
        BeanUtils.copyProperties(saleDTO, saleDO);
        boolean updateSale =saleMapper.updateSaleOrder(saleDO);
        boolean createSub = true;
        boolean updateSub = true;
        boolean deleteSub = true;
        if(!CollectionUtils.isEmpty(createSubDTOS)){
            List<SaleSubDO> createSubDOS = DTOToDO(createSubDTOS);
            createSub = saleMapper.batchInsertSub(createSubDOS);
        }
        if(!CollectionUtils.isEmpty(updateSubDTOS)){
            List<SaleSubDO> updateSubDOS = DTOToDO(updateSubDTOS);
            updateSub = saleMapper.batchUpdateSub(updateSubDOS);
        }
        if(!CollectionUtils.isEmpty(deleteSubDTOS)){
            List<SaleSubDO> deleteSubDOS = DTOToDO(deleteSubDTOS);
            deleteSub = saleMapper.batchDeleteSub(deleteSubDOS);
        }
        return updateSale && createSub && updateSub && deleteSub;
    }

    public List<SaleSubDO> DTOToDO(List<SaleSubDTO> saleSubDTOS){
        List<SaleSubDO> saleSubDOS = saleSubDTOS.stream().map(saleSubDTO -> {
            SaleSubDO saleSubDO = new SaleSubDO();
            BeanUtils.copyProperties(saleSubDTO, saleSubDO);
            return saleSubDO;
        }).collect(Collectors.toList());
        return saleSubDOS;
    }
}
