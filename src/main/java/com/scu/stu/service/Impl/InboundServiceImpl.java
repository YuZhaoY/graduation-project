package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.common.Result;
import com.scu.stu.mapper.InboundMapper;
import com.scu.stu.mapper.RelationMapper;
import com.scu.stu.pojo.DO.InboundDO;
import com.scu.stu.pojo.DO.InboundSubDO;
import com.scu.stu.pojo.DO.RelationDO;
import com.scu.stu.pojo.DO.queryParam.InboundQuery;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DTO.InboundDTO;
import com.scu.stu.pojo.DTO.InboundSubDTO;
import com.scu.stu.service.InboundService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InboundServiceImpl implements InboundService {

    @Resource
    private InboundMapper inboundMapper;

    @Override
    public List<InboundDTO> query(InboundQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<InboundDO> inboundDOList = inboundMapper.query(query);
        if(inboundDOList != null && !CollectionUtils.isEmpty(inboundDOList)){
            List<InboundDTO> inboundDTOList = inboundDOList.stream().map(inboundDO -> {
                InboundDTO inboundDTO = new InboundDTO();
                BeanUtils.copyProperties(inboundDO, inboundDTO);
                return inboundDTO;
            }).collect(Collectors.toList());
            PageInfo<InboundDTO> pageInfo = new PageInfo<>(inboundDTOList);
            return pageInfo.getList();
        }
        return null;
    }

    @Override
    public int total(InboundQuery query) {
        return inboundMapper.total(query);
    }

    @Override
    public List<InboundSubDTO> detail(String inboundId) {
        List<InboundSubDO> inboundSubDOS = inboundMapper.querySub(inboundId);
        if(inboundSubDOS != null && !CollectionUtils.isEmpty(inboundSubDOS)){
            List<InboundSubDTO> inboundSubDTOS = inboundSubDOS.stream().map(inboundSubDO -> {
                InboundSubDTO inboundSubDTO = new InboundSubDTO();
                BeanUtils.copyProperties(inboundSubDO, inboundSubDTO);
                return inboundSubDTO;
            }).collect(Collectors.toList());
            return inboundSubDTOS;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(InboundDTO inboundDTO, List<InboundSubDTO> inboundSubDTOList) {
        InboundDO inboundDO = new InboundDO();
        BeanUtils.copyProperties(inboundDTO, inboundDO);
        List<InboundSubDO> inboundSubDOList = inboundSubDTOList.stream().map(inboundSubDTO -> {
            InboundSubDO inboundSubDO = new InboundSubDO();
            BeanUtils.copyProperties(inboundSubDTO, inboundSubDO);
            return inboundSubDO;
        }).collect(Collectors.toList());
        return inboundMapper.create(inboundDO) && inboundMapper.createSub(inboundSubDOList);

    }

}
