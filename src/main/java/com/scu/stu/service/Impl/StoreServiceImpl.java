package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.mapper.StoreMapper;
import com.scu.stu.pojo.DO.StoreDO;
import com.scu.stu.pojo.DO.queryParam.StoreQuery;
import com.scu.stu.pojo.DTO.StoreDTO;
import com.scu.stu.service.StoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    @Resource
    private StoreMapper storeMapper;

    @Override
    public List<StoreDTO> query(StoreQuery query) {
        PageHelper.startPage(query.getPage(),query.getLimit());
        List<StoreDO> storeDOS = storeMapper.query(query);
        if(storeDOS == null || CollectionUtils.isEmpty(storeDOS)){
            return null;
        } else {
            List<StoreDTO> storeDTOS = storeDOS.stream().map(storeDO -> {
                StoreDTO storeDTO = new StoreDTO();
                BeanUtils.copyProperties(storeDO, storeDTO);
                return storeDTO;
            }).collect(Collectors.toList());
            PageInfo<StoreDTO> pageInfo = new PageInfo<>(storeDTOS);
            return pageInfo.getList();
        }
    }

    @Override
    public List<StoreDTO> queryAll(StoreQuery query) {
        List<StoreDO> storeDOS = storeMapper.query(query);
        if(storeDOS == null || CollectionUtils.isEmpty(storeDOS)){
            return null;
        } else {
            List<StoreDTO> storeDTOS = storeDOS.stream().map(storeDO -> {
                StoreDTO storeDTO = new StoreDTO();
                BeanUtils.copyProperties(storeDO, storeDTO);
                return storeDTO;
            }).collect(Collectors.toList());
            return storeDTOS;
        }
    }

    @Override
    public int total(StoreQuery query) {
        return storeMapper.total(query);
    }

    @Override
    public boolean update(StoreDTO storeDTO) {
        StoreDO storeDO = new StoreDO();
        BeanUtils.copyProperties(storeDTO, storeDO);
        return storeMapper.update(storeDO);
    }

    @Override
    public boolean delete(String storeId) {
        return storeMapper.delete(storeId);
    }

    @Override
    public boolean create(StoreDTO storeDTO) {
        StoreDO storeDO = new StoreDO();
        BeanUtils.copyProperties(storeDTO, storeDO);
        return storeMapper.create(storeDO);
    }

    @Override
    public String getStoreId() {
        return storeMapper.queryStoreId();
    }
}
