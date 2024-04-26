package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.common.ReplenishmentStatus;
import com.scu.stu.mapper.ReplenishmentMapper;
import com.scu.stu.pojo.DO.ReplenishmentDO;
import com.scu.stu.pojo.DO.ReplenishmentSubDO;
import com.scu.stu.pojo.DO.queryParam.ReplenishmentQuery;
import com.scu.stu.pojo.DO.queryParam.ReplenishmentSubQuery;
import com.scu.stu.pojo.DTO.ReplenishmentDTO;
import com.scu.stu.pojo.DTO.ReplenishmentSubDTO;
import com.scu.stu.service.ReplenishmentService;
import com.scu.stu.utils.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplenishmentServiceImpl implements ReplenishmentService {

    @Resource
    private ReplenishmentMapper replenishmentMapper;

    @Override
    public List<ReplenishmentDTO> query(ReplenishmentQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<ReplenishmentDO> replenishmentDOS = replenishmentMapper.query(query);
        if(!CollectionUtils.isEmpty(replenishmentDOS)){
            List<ReplenishmentDTO> replenishmentDTOS = replenishmentDOS.stream().map(replenishmentDO -> {
                ReplenishmentDTO replenishmentDTO = new ReplenishmentDTO();
                BeanUtils.copyProperties(replenishmentDO, replenishmentDTO);
                replenishmentDTO.setStatus(ReplenishmentStatus.getDescByCode(replenishmentDO.getStatus()));
                replenishmentDTO.setExpiration(DateUtils.format(replenishmentDO.getExpiration()));
                replenishmentDTO.setStartExpiration(DateUtils.format(replenishmentDO.getStartExpiration()));
                return replenishmentDTO;
            }).collect(Collectors.toList());
            PageInfo<ReplenishmentDTO> pageInfo = new PageInfo<>(replenishmentDTOS);
            return pageInfo.getList();
        }
        return null;
    }

    @Override
    public int total(ReplenishmentQuery query) {
        return replenishmentMapper.total(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createReplenishment(ReplenishmentDTO replenishmentDTO, List<ReplenishmentSubDTO> replenishmentSubDTOS) throws ParseException {
        ReplenishmentDO replenishmentDO = new ReplenishmentDO();
        BeanUtils.copyProperties(replenishmentDTO, replenishmentDO);
        replenishmentDO.setExpiration(DateUtils.parse(replenishmentDTO.getExpiration()));
        replenishmentDO.setStartExpiration(DateUtils.parse(replenishmentDTO.getStartExpiration()));
        replenishmentDO.setStatus(ReplenishmentStatus.getCodeByDesc(replenishmentDTO.getStatus()));
        List<ReplenishmentSubDO> replenishmentSubDOS = DTOToDO(replenishmentSubDTOS);
        return replenishmentMapper.create(replenishmentDO) && replenishmentMapper.batchCreateSub(replenishmentSubDOS);
    }

    @Override
    @Transactional
    public boolean updateReplenishment(
            ReplenishmentDTO replenishmentDTO,
            List<ReplenishmentSubDTO> updateSubDTOS,
            List<ReplenishmentSubDTO> createSubDTOS,
            List<ReplenishmentSubDTO> deleteSubDTOS
            ) throws ParseException {
        ReplenishmentDO replenishmentDO = new ReplenishmentDO();
        BeanUtils.copyProperties(replenishmentDTO, replenishmentDO);
        replenishmentDO.setExpiration(DateUtils.parse(replenishmentDTO.getExpiration()));
        replenishmentDO.setStartExpiration(DateUtils.parse(replenishmentDTO.getStartExpiration()));
        replenishmentDO.setStatus(ReplenishmentStatus.getCodeByDesc(replenishmentDTO.getStatus()));
        boolean updateReplenishment = replenishmentMapper.update(replenishmentDO);
        boolean createSub = true;
        boolean updateSub = true;
        boolean deleteSub = true;
        if(!CollectionUtils.isEmpty(createSubDTOS)){
            List<ReplenishmentSubDO> createSubDOS = DTOToDO(createSubDTOS);
            createSub = replenishmentMapper.batchCreateSub(createSubDOS);
        }
        if(!CollectionUtils.isEmpty(updateSubDTOS)){
            List<ReplenishmentSubDO> updateSubDOS = DTOToDO(updateSubDTOS);
            updateSub = replenishmentMapper.batchUpdateSub(updateSubDOS);
        }
        if(!CollectionUtils.isEmpty(createSubDTOS)){
            List<ReplenishmentSubDO> deleteSubDOS = DTOToDO(deleteSubDTOS);
            deleteSub = replenishmentMapper.batchDeleteSub(deleteSubDOS);
        }
        return updateReplenishment && createSub && updateSub && deleteSub;
    }

    @Override
    public boolean updateReplenishment(ReplenishmentDTO replenishmentDTO) throws ParseException {
        ReplenishmentDO replenishmentDO = new ReplenishmentDO();
        BeanUtils.copyProperties(replenishmentDTO, replenishmentDO);
        if(replenishmentDTO.getStatus() !=null && !"".equals(replenishmentDTO.getStatus())){
            replenishmentDO.setStatus(ReplenishmentStatus.getCodeByDesc(replenishmentDTO.getStatus()));
        }
        if(replenishmentDTO.getExpiration() !=null && !"".equals(replenishmentDTO.getExpiration())) {
            replenishmentDO.setExpiration(DateUtils.parse(replenishmentDTO.getExpiration()));
        }
        if(replenishmentDTO.getStartExpiration() !=null && !"".equals(replenishmentDTO.getStartExpiration())) {
            replenishmentDO.setStartExpiration(DateUtils.parse(replenishmentDTO.getStartExpiration()));
        }
        return replenishmentMapper.update(replenishmentDO);
    }

    @Override
    public boolean updateInvalid() {
        return replenishmentMapper.updateInvalid();
    }

    @Override
    public boolean updateValid() {
        return replenishmentMapper.updateValid();
    }

    @Override
    public List<ReplenishmentSubDTO> querySub(ReplenishmentSubQuery query) {
        List<ReplenishmentSubDO> replenishmentSubDOS = replenishmentMapper.querySub(query);
        if(!CollectionUtils.isEmpty(replenishmentSubDOS)){
            List<ReplenishmentSubDTO> replenishmentSubDTOS = replenishmentSubDOS.stream().map(replenishmentSubDO -> {
                ReplenishmentSubDTO replenishmentSubDTO = new ReplenishmentSubDTO();
                BeanUtils.copyProperties(replenishmentSubDO, replenishmentSubDTO);
                return replenishmentSubDTO;
            }).collect(Collectors.toList());
            return replenishmentSubDTOS;
        }
        return null;
    }

    @Override
    public List<ReplenishmentSubDTO> batchQuerySub(List<String> replenishmentIdList) {
        List<ReplenishmentSubDO> replenishmentSubDOS = replenishmentMapper.batchQuerySub(replenishmentIdList);
        if(!CollectionUtils.isEmpty(replenishmentSubDOS)){
            List<ReplenishmentSubDTO> replenishmentSubDTOS = replenishmentSubDOS.stream().map(replenishmentSubDO -> {
                ReplenishmentSubDTO replenishmentSubDTO = new ReplenishmentSubDTO();
                BeanUtils.copyProperties(replenishmentSubDO, replenishmentSubDTO);
                return replenishmentSubDTO;
            }).collect(Collectors.toList());
            return replenishmentSubDTOS;
        }
        return null;
    }

    @Override
    public boolean createReplenishmentSub(List<ReplenishmentSubDTO> replenishmentSubDTOS) {
        List<ReplenishmentSubDO> replenishmentSubDOS = DTOToDO(replenishmentSubDTOS);
        return replenishmentMapper.batchCreateSub(replenishmentSubDOS);
    }

    @Override
    public boolean updateReplenishmentSub(List<ReplenishmentSubDTO> replenishmentSubDTOS) {
        List<ReplenishmentSubDO> replenishmentSubDOS = DTOToDO(replenishmentSubDTOS);
        return replenishmentMapper.batchUpdateSub(replenishmentSubDOS);
    }

    @Override
    public boolean deleteReplenishmentSub(List<ReplenishmentSubDTO> replenishmentSubDTOS) {
        List<ReplenishmentSubDO> replenishmentSubDOS = DTOToDO(replenishmentSubDTOS);
        return replenishmentMapper.batchDeleteSub(replenishmentSubDOS);
    }

    public List<ReplenishmentSubDO> DTOToDO(List<ReplenishmentSubDTO> replenishmentSubDTOS){
        List<ReplenishmentSubDO> replenishmentSubDOS = replenishmentSubDTOS.stream().map(replenishmentSubDTO -> {
            ReplenishmentSubDO replenishmentSubDO = new ReplenishmentSubDO();
            BeanUtils.copyProperties(replenishmentSubDTO, replenishmentSubDO);
            return replenishmentSubDO;
        }).collect(Collectors.toList());
        return replenishmentSubDOS;
    }
}
