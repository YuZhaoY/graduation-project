package com.scu.stu.service.Impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.mapper.ItemMapper;
import com.scu.stu.pojo.DO.ItemDO;
import com.scu.stu.pojo.DO.queryParam.ItemQuery;
import com.scu.stu.pojo.DTO.ItemDTO;
import com.scu.stu.service.ItemService;
import com.scu.stu.utils.CategoryUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ItemMapper itemMapper;

    @Override
    public List<ItemDTO> query(ItemQuery query) {
        if(query.getPage() != 0 && query.getLimit() != 0){
            PageHelper.startPage(query.getPage(),query.getLimit());
        }
        List<ItemDO> itemDOS = itemMapper.query(query);
        if(!itemDOS.isEmpty()){
            List<ItemDTO> itemDTOS = itemDOS.stream().map(itemDO -> {
                ItemDTO itemDTO = new ItemDTO();
                BeanUtils.copyProperties(itemDO,itemDTO);
                itemDTO.setSpecification(JSON.parseArray(itemDO.getSpecification(), String.class));
                itemDTO.setCategory(CategoryUtils.getCategoryDesc(itemDO.getCategory()));
                return itemDTO;
            }).collect(Collectors.toList());
            PageInfo<ItemDTO> pageInfo = new PageInfo<>(itemDTOS);
            return pageInfo.getList();
        }else {
            return null;
        }
    }

    @Override
    public int total(ItemQuery query) {
        return itemMapper.total(query);
    }

    @Override
    public boolean create(ItemDTO itemDTO) {
        ItemDO itemDO = new ItemDO();
        itemDO.setItemId(itemDTO.getItemId());
        itemDO.setCategory(itemDTO.getCategory());
        itemDO.setName(itemDTO.getName());
        itemDO.setPicUrl(itemDTO.getPicUrl());
        itemDO.setUnit(itemDTO.getUnit());
        itemDO.setSpecification(JSON.toJSONString(itemDTO.getSpecification()));
        return itemMapper.create(itemDO);
    }

    @Override
    public String getItemId() {
        return itemMapper.queryItemId();
    }

    @Override
    public boolean update(ItemDTO itemDTO) {
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemDTO, itemDO);
        if(itemDTO.getSpecification() != null){
            itemDO.setSpecification(JSON.toJSONString(itemDTO.getSpecification()));
        }
        return itemMapper.update(itemDO);
    }

    @Override
    public boolean delete(String itemId) {
        return itemMapper.delete(itemId);
    }
}
