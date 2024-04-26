package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.common.Result;
import com.scu.stu.mapper.RefundMapper;
import com.scu.stu.mapper.RelationMapper;
import com.scu.stu.pojo.DO.RefundDO;
import com.scu.stu.pojo.DO.RefundSubDO;
import com.scu.stu.pojo.DO.RelationDO;
import com.scu.stu.pojo.DO.queryParam.RefundQuery;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DTO.RefundDTO;
import com.scu.stu.pojo.DTO.RefundSubDTO;
import com.scu.stu.service.RefundService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RefundServiceImpl implements RefundService {

    @Resource
    private RefundMapper refundMapper;

    @Override
    public List<RefundDTO> query(RefundQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<RefundDO> refundDOList = refundMapper.query(query);
        if(refundDOList != null && !CollectionUtils.isEmpty(refundDOList)){
            List<RefundDTO> refundDTOList = refundDOList.stream().map(refundDO -> {
                RefundDTO refundDTO = new RefundDTO();
                BeanUtils.copyProperties(refundDO, refundDTO);
                return refundDTO;
            }).collect(Collectors.toList());
            PageInfo<RefundDTO> pageInfo = new PageInfo<>(refundDTOList);
            return pageInfo.getList();
        }
        return null;
    }

    @Override
    public int total(RefundQuery query) {
        return refundMapper.total(query);
    }

    @Override
    public List<RefundSubDTO> detail(String refundId) {
        List<RefundSubDO> refundSubDOS = refundMapper.querySub(refundId);
        if(refundSubDOS != null && !CollectionUtils.isEmpty(refundSubDOS)){
            List<RefundSubDTO> refundSubDTOS = refundSubDOS.stream().map(refundSubDO -> {
                RefundSubDTO refundSubDTO = new RefundSubDTO();
                BeanUtils.copyProperties(refundSubDO, refundSubDTO);
                return refundSubDTO;
            }).collect(Collectors.toList());
            return refundSubDTOS;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(RefundDTO refundDTO, List<RefundSubDTO> refundSubDTOList) {
        RefundDO refundDO = new RefundDO();
        BeanUtils.copyProperties(refundDTO, refundDO);
        List<RefundSubDO> refundSubDOList = refundSubDTOList.stream().map(refundSubDTO -> {
            RefundSubDO refundSubDO = new RefundSubDO();
            BeanUtils.copyProperties(refundSubDTO, refundSubDO);
            return refundSubDO;
        }).collect(Collectors.toList());
        return refundMapper.create(refundDO) && refundMapper.createSub(refundSubDOList);
    }
}
