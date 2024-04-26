package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.mapper.RelationMapper;
import com.scu.stu.pojo.DO.RelationDO;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DTO.RelationDTO;
import com.scu.stu.service.RelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelationServiceImpl implements RelationService {

    @Resource
    private RelationMapper relationMapper;

    @Override
    public List<RelationDTO> queryByBookingList(List<String> bookingList) {
        List<RelationDO> relationDOList = relationMapper.queryByBookingList(bookingList);
        if(relationDOList != null && !CollectionUtils.isEmpty(relationDOList)){
            List<RelationDTO> relationDTOList = relationDOList.stream().map(relationDO -> {
                RelationDTO relationDTO = new RelationDTO();
                BeanUtils.copyProperties(relationDO, relationDTO);
                return relationDTO;
            }).collect(Collectors.toList());
            return relationDTOList;
        }
        return null;
    }

    @Override
    public List<RelationDTO> queryBySaleIdList(List<String> saleIdList) {
        List<RelationDO> relationDOList = relationMapper.queryBySaleIdList(saleIdList);
        if(relationDOList != null && !CollectionUtils.isEmpty(relationDOList)){
            List<RelationDTO> relationDTOList = relationDOList.stream().map(relationDO -> {
                RelationDTO relationDTO = new RelationDTO();
                BeanUtils.copyProperties(relationDO, relationDTO);
                return relationDTO;
            }).collect(Collectors.toList());
            return relationDTOList;
        }
        return null;
    }
    @Override
    public List<RelationDTO> query(RelationQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<RelationDO> relationDOList = relationMapper.query(query);
        if(relationDOList != null && !CollectionUtils.isEmpty(relationDOList)){
            List<RelationDTO> relationDTOList = relationDOList.stream().map(relationDO -> {
                RelationDTO relationDTO = new RelationDTO();
                BeanUtils.copyProperties(relationDO, relationDTO);
                return relationDTO;
            }).collect(Collectors.toList());
            PageInfo<RelationDTO> pageInfo = new PageInfo<>(relationDTOList);
            return pageInfo.getList();
        }
        return null;
    }

    @Override
    public int total(RelationQuery query) {
        return relationMapper.total(query);
    }

    @Override
    public boolean create(RelationDTO relationDTO) {
        RelationDO relationDO = new RelationDO();
        BeanUtils.copyProperties(relationDTO, relationDO);
        return relationMapper.create(relationDO);
    }

    @Override
    public boolean update(RelationDTO relationDTO) {
        RelationDO relationDO = new RelationDO();
        BeanUtils.copyProperties(relationDTO, relationDO);
        return relationMapper.update(relationDO);
    }
}
