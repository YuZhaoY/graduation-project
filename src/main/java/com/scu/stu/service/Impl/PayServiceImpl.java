package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.mapper.PayMapper;
import com.scu.stu.pojo.DO.PayDO;
import com.scu.stu.pojo.DO.queryParam.PayQuery;
import com.scu.stu.pojo.DTO.PayDTO;
import com.scu.stu.service.PayService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayServiceImpl implements PayService {

    @Resource
    private PayMapper payMapper;
    @Override
    public List<PayDTO> query(PayQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<PayDO> payDOList = payMapper.query(query);
        if(payDOList!= null && !CollectionUtils.isEmpty(payDOList)){
            List<PayDTO> payDTOList = payDOList.stream().map(payDO -> {
                PayDTO payDTO = new PayDTO();
                BeanUtils.copyProperties(payDO, payDTO);
                return payDTO;
            }).collect(Collectors.toList());
            PageInfo<PayDTO> pageInfo = new PageInfo<>(payDTOList);
            return pageInfo.getList();
        }
        return null;
    }

    @Override
    public int total(PayQuery query) {
        return payMapper.total(query);
    }

    @Override
    public boolean create(PayDTO payDTO) {
        PayDO payDO = new PayDO();
        BeanUtils.copyProperties(payDTO, payDO);
        return payMapper.create(payDO);
    }

    @Override
    public boolean update(PayDTO payDTO) {
        PayDO payDO = new PayDO();
        BeanUtils.copyProperties(payDTO, payDO);
        return payMapper.update(payDO);
    }
}
